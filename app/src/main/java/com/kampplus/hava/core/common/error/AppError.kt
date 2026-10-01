package com.kampplus.hava.core.common.error

/** Uygulama genelindeki hata sözlüğü. UI metinleri presentation katmanında üretilir. */
sealed interface AppError {
    data object Network : AppError

    data class Server(
        val code: Int
    ) : AppError

    data object RateLimited : AppError

    data object NotFound : AppError

    data object Parse : AppError

    data class Unknown(
        val cause: Throwable
    ) : AppError
}
