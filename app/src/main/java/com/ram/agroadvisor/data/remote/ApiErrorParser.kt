package com.ram.agroadvisor.data.remote

import org.json.JSONObject
import retrofit2.HttpException

/**
 * Parses backend error bodies. The AgroAdvisor API returns either:
 *   - `{ "message": "Invalid email or password." }` (custom errors)
 *   - RFC7807 ProblemDetails `{ "title": "...", "detail": "..." }`
 */
internal object ApiErrorParser {
    fun parse(e: HttpException, fallback: String): String {
        val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
        if (body.isNullOrBlank()) return fallback
        return runCatching {
            val json = JSONObject(body)
            json.optString("message")
                .ifBlank { json.optString("detail") }
                .ifBlank { json.optString("title") }
                .ifBlank { fallback }
        }.getOrDefault(fallback)
    }
}
