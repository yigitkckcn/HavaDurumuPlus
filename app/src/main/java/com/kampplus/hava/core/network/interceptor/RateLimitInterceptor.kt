package com.kampplus.hava.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Open-Meteo 429 (Too Many Requests) döndüğünde exponential back-off ile yeniden dener.
 * `Retry-After` başlığı varsa ona uyar, yoksa [initialDelayMs]'den başlar.
 */
class RateLimitInterceptor(
    private val maxRetries: Int = MAX_RETRIES,
    private val initialDelayMs: Long = INITIAL_DELAY_MS
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var attempt = 0

        while (response.code == HTTP_TOO_MANY_REQUESTS && attempt < maxRetries) {
            response.close()
            val retryAfterMs = response.header("Retry-After")
                ?.toLongOrNull()
                ?.times(SECONDS_TO_MS)
                ?: (initialDelayMs * (1L shl attempt))

            try {
                Thread.sleep(retryAfterMs.coerceAtMost(MAX_DELAY_MS))
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IOException("Rate-limit retry interrupted")
            }

            attempt++
            response = chain.proceed(request)
        }
        return response
    }

    private companion object {
        const val HTTP_TOO_MANY_REQUESTS = 429
        const val MAX_RETRIES = 4
        const val INITIAL_DELAY_MS = 2_000L
        const val MAX_DELAY_MS = 16_000L
        const val SECONDS_TO_MS = 1_000L
    }
}
