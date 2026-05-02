package com.ram.agroadvisor.data.model

data class RegisterRequest(
    val name: String,
    val surname: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

/** Login response — backend returns `{ "token": "..." }`. */
data class AuthResponse(
    val token: String
)

/** Generic message envelope used by register & error responses. */
data class MessageResponse(
    val message: String?
)
