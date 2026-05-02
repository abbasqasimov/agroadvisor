package com.ram.agroadvisor.ui.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.agroadvisor.data.local.TokenManager
import com.ram.agroadvisor.data.model.LoginRequest
import com.ram.agroadvisor.data.model.RegisterRequest
import com.ram.agroadvisor.data.remote.AgroApi
import com.ram.agroadvisor.data.remote.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object Success : AuthUiState()
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
                // Preserve any name we already collected; otherwise fall back to email.
                if (tokenManager.getEmail().isNullOrBlank()) {
                    tokenManager.saveUserInfo(email, tokenManager.getFullName().orEmpty())
                } else {
                    tokenManager.saveUserInfo(email, tokenManager.getFullName().orEmpty())
                }
                _uiState.value = AuthUiState.Success
            } catch (e: HttpException) {
                val msg = when (e.code()) {
                    401 -> ApiErrorParser.parse(e, "Email və ya şifrə yanlışdır")
                    404 -> "İstifadəçi tapılmadı"
                    else -> ApiErrorParser.parse(e, "Xəta: ${e.code()}")
                }
                _uiState.value = AuthUiState.Error(msg)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Bağlantı xətası")
            }
        }
    }

    /**
     * Registers the user, then immediately logs in to obtain a JWT.
     * Backend's register response only contains a confirmation `message`,
     * so the explicit follow-up login is required to authenticate the session.
     */
    fun register(name: String, surname: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                agroApi.register(
                    RegisterRequest(
                        name = name.trim(),
                        surname = surname.trim(),
                        email = email,
                        password = password
                    )
                )
                // Persist the full name now (login API doesn't return it).
                tokenManager.saveUserInfo(email, "${name.trim()} ${surname.trim()}".trim())

                val loginResp = agroApi.login(LoginRequest(email, password))
                tokenManager.saveToken(loginResp.token)

                _uiState.value = AuthUiState.Success
            } catch (e: HttpException) {
                val msg = when (e.code()) {
                    400 -> ApiErrorParser.parse(e, "Bu email artıq qeydiyyatdan keçib")
                    409 -> "Bu email artıq qeydiyyatdan keçib"
                    else -> ApiErrorParser.parse(e, "Xəta: ${e.code()}")
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
