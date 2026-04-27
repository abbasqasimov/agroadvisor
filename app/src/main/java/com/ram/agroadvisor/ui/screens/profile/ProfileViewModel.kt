package com.ram.agroadvisor.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.ram.agroadvisor.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {
    fun getFullName(): String = tokenManager.getFullName() ?: "İstifadəçi"
    fun getEmail(): String = tokenManager.getEmail() ?: ""
    fun logout() = tokenManager.clearAll()
}
