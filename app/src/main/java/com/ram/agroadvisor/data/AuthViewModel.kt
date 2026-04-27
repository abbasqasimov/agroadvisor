package com.ram.agroadvisor.ui.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.agroadvisor.data.local.TokenManager
import com.ram.agroadvisor.data.model.LoginRequest
import com.ram.agroadvisor.data.model.RegisterRequest
import com.ram.agroadvisor.data.remote.AgroApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val agroApi: AgroApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = agroApi.login(LoginRequest(email = email, password = password))
                tokenManager.saveToken(response.token)
                tokenManager.saveUserInfo(email, "")
                _uiState.value = AuthUiState.Success
            } catch (e: retrofit2.HttpException) {
                val msg = when (e.code()) {
                    401 -> "Email və ya şifrə yanlışdır"
                    404 -> "İstifadəçi tapılmadı"
                    else -> "Xəta: ${e.code()}"
                }
                _uiState.value = AuthUiState.Error(msg)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Bağlantı xətası")
            }
        }
    }

    fun register(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = agroApi.register(
                    RegisterRequest(email = email, password = password, fullName = fullName)
                )
                tokenManager.saveToken(response.token)
                tokenManager.saveUserInfo(email, fullName)
                _uiState.value = AuthUiState.Success
            } catch (e: retrofit2.HttpException) {
                val msg = when (e.code()) {
                    400 -> "Bu email artıq qeydiyyatdan keçib"
                    else -> "Xəta: ${e.code()}"
                }
                _uiState.value = AuthUiState.Error(msg)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Bağlantı xətası")
            }
        }
    }

    fun reset() {
        _uiState.value = AuthUiState.Idle
    }
}
