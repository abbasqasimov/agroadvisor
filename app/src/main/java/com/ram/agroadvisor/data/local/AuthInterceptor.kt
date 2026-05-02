package com.ram.agroadvisor.data.local

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Adds `Authorization: Bearer <token>` to every outgoing request that hits
 * the AgroAdvisor backend, EXCEPT the public auth endpoints (login / register).
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath

        val isPublic = path.endsWith("/api/Auth/login") ||
                path.endsWith("/api/Auth/register")

        if (isPublic) {
            return chain.proceed(original)
        }

        val token = tokenManager.getToken()
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
