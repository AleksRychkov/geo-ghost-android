package dev.aleksrychkov.geoghost.core.exception

import kotlin.Result.Companion.success
import kotlin.coroutines.cancellation.CancellationException

inline fun <T, R> T.safeRunCatching(block: T.() -> R): Result<R> {
    return try {
        success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}