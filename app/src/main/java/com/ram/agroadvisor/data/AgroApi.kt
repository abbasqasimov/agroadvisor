package com.ram.agroadvisor.data.remote

import com.ram.agroadvisor.data.CalculatorRequest
import com.ram.agroadvisor.data.CalculatorResponse
import com.ram.agroadvisor.data.model.AnalyzeResponse
import com.ram.agroadvisor.data.model.AuthResponse
import com.ram.agroadvisor.data.model.ChatMessageDto
import com.ram.agroadvisor.data.model.ChatRequestDto
import com.ram.agroadvisor.data.model.ChatResponseDto
import com.ram.agroadvisor.data.model.LoginRequest
import com.ram.agroadvisor.data.model.MessageResponse
import com.ram.agroadvisor.data.model.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AgroApi {

    // ---- Auth ----
    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): MessageResponse

    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // ---- Calculator ----
    @POST("api/Calculator/calculate")
    suspend fun calculate(@Body request: CalculatorRequest): CalculatorResponse

    // ---- Chat ----
    @POST("api/Chat")
    suspend fun chat(@Body request: ChatRequestDto): ChatResponseDto

    @GET("api/Chat/{sessionId}/history")
    suspend fun getChatHistory(@Path("sessionId") sessionId: String): List<ChatMessageDto>

    @GET("api/Chat/sessions")
    suspend fun getChatSessions(): List<String>

    @DELETE("api/Chat/{sessionId}")
    suspend fun deleteChatSession(@Path("sessionId") sessionId: String)

    // ---- IA Analyze ----
    @Multipart
    @POST("api/IA/Analyze")
    suspend fun analyze(
        @Part file: MultipartBody.Part,
        @Part("Prompt") prompt: RequestBody
    ): AnalyzeResponse
}
