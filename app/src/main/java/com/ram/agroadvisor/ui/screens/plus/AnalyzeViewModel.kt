package com.ram.agroadvisor.ui.screens.plus

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.agroadvisor.data.model.AnalyzeResponse
import com.ram.agroadvisor.data.remote.AgroApi
import com.ram.agroadvisor.data.remote.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AnalyzeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val agroApi: AgroApi
) : ViewModel() {

    sealed class UiState {
        data object Idle : UiState()
        data object Loading : UiState()
        data class Success(val data: AnalyzeResponse) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun reset() {
        _state.value = UiState.Idle
    }

    /**
     * Reads the chosen image as bytes, posts it as multipart with the prompt,
     * and exposes the typed result through [state].
     */
    fun analyze(
        uri: Uri,
        prompt: String = "Identify the plant and any visible disease in this photograph. " +
                "Provide the plant name, disease name, a confidence score (0-100), " +
                "and a brief, plain-language explanation."
    ) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val (bytes, mime, fileName) = withContext(Dispatchers.IO) {
                    val resolver = appContext.contentResolver
                    val mime = resolver.getType(uri) ?: "image/jpeg"
                    val ext = when {
                        mime.contains("png", ignoreCase = true) -> "png"
                        mime.contains("webp", ignoreCase = true) -> "webp"
                        else -> "jpg"
                    }
                    val raw = resolver.openInputStream(uri)?.use { it.readBytes() }
                        ?: error("Şəkil oxunmadı")
                    Triple(raw, mime, "upload.$ext")
                }

                val mediaType = mime.toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull()
                val filePart = MultipartBody.Part.createFormData(
                    "File",
                    fileName,
                    bytes.toRequestBody(mediaType)
                )
                val promptPart = prompt.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = agroApi.analyze(filePart, promptPart)
                _state.value = UiState.Success(response)
            } catch (e: HttpException) {
                val msg = when (e.code()) {
                    401 -> "Sessiyanın müddəti bitib. Yenidən daxil olun"
                    413 -> "Şəkil çox böyükdür"
                    else -> ApiErrorParser.parse(e, "Server xətası: ${e.code()}")
                }
                _state.value = UiState.Error(msg)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Analiz alınmadı")
            }
        }
    }
}
