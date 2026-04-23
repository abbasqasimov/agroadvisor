package com.ram.agroadvisor.ui.screens.authentication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.agroadvisor.data.local.TokenManager
import com.ram.agroadvisor.data.model.LoginRequest
import com.ram.agroadvisor.data.model.RegisterRequest
import com.ram.agroadvisor.data.remote.AgroRetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = AgroRetrofitInstance.api.login(
                    LoginRequest(email = email, password = password)
                )
                TokenManager.saveToken(context, response.token)
                TokenManager.saveUserInfo(context, email, "")
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

    fun register(context: Context, email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = AgroRetrofitInstance.api.register(
                    RegisterRequest(email = email, password = password, fullName = fullName)
                )
                TokenManager.saveToken(context, response.token)
                TokenManager.saveUserInfo(context, email, fullName)
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