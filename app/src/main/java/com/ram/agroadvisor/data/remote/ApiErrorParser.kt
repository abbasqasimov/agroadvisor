package com.ram.agroadvisor.data.remote

import org.json.JSONObject
import retrofit2.HttpException

/**
 * Parses backend error bodies. The AgroAdvisor API returns either:
 *   - `{ "message": "Current password is incorrect." }` (custom errors)
 *   - RFC7807 ProblemDetails with an `errors` map:
 *     `{ "title": "...", "errors": { "FieldName": ["msg1", "msg2"] } }`
 */
internal object ApiErrorParser {
    fun parse(e: HttpException, fallback: String): String {
        val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
        if (body.isNullOrBlank()) return fallback
        return runCatching {
            val json = JSONObject(body)

            // Prefer the simple `message` envelope when present.
            val message = json.optString("message")
            if (message.isNotBlank()) return@runCatching message

            // Then try to flatten the first field-level validation error.
            json.optJSONObject("errors")?.let { errors ->
                val firstKey = errors.keys().asSequence().firstOrNull()
                if (firstKey != null) {
                    val list = errors.optJSONArray(firstKey)
                    val first = list?.optString(0)
                    if (!first.isNullOrBlank()) return@runCatching first
                }
            }

            json.optString("detail")
                .ifBlank { json.optString("title") }
                .ifBlank { fallback }
        }.getOrDefault(fallback)
    }
}
