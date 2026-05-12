package com.ram.agroadvisor.ui.screens.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.agroadvisor.data.model.UpdatePasswordRequest
import com.ram.agroadvisor.data.remote.AgroApi
import com.ram.agroadvisor.data.remote.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed class UpdatePasswordState {
    data object Idle : UpdatePasswordState()
    data object Loading : UpdatePasswordState()
    data class Success(val message: String) : UpdatePasswordState()
    data class Error(val message: String) : UpdatePasswordState()
}

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val agroApi: AgroApi
) : ViewModel() {

    private val _state = MutableStateFlow<UpdatePasswordState>(UpdatePasswordState.Idle)
    val state: StateFlow<UpdatePasswordState> = _state

    fun updatePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ) {
        viewModelScope.launch {
            _state.value = UpdatePasswordState.Loading
            try {
                val resp = agroApi.updatePassword(
                    UpdatePasswordRequest(
                        currentPassword = currentPassword,
                        newPassword = newPassword,
                        confirmNewPassword = confirmNewPassword
                    )
                )
                _state.value = UpdatePasswordState.Success(
                    resp.message ?: "Şifrə uğurla yeniləndi"
                )
            } catch (e: HttpException) {
                val msg = when (e.code()) {
                    400 -> ApiErrorParser.parse(e, "Cari şifrə yanlışdır")
                    401 -> "Sessiyanın müddəti bitib. Yenidən daxil olun"
                    else -> ApiErrorParser.parse(e, "Xəta: ${e.code()}")
                }
                _state.value = UpdatePasswordState.Error(msg)
            } catch (e: Exception) {
                _state.value = UpdatePasswordState.Error(e.message ?: "Bağlantı xətası")
            }
        }
    }

    fun reset() {
        _state.value = UpdatePasswordState.Idle
    }
}
