package com.ram.agroadvisor.data.remote

import com.ram.agroadvisor.data.CalculatorRequest
import com.ram.agroadvisor.data.CalculatorResponse
import com.ram.agroadvisor.data.model.AuthResponse
import com.ram.agroadvisor.data.model.LoginRequest
import com.ram.agroadvisor.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AgroApi {
    @POST("api/Calculator/calculate")
    suspend fun calculate(@Body request: CalculatorRequest): CalculatorResponse

    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
