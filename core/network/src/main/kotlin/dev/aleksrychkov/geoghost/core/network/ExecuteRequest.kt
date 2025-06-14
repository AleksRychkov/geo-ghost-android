package dev.aleksrychkov.geoghost.core.network

import dev.aleksrychkov.geoghost.core.exception.safeRunCatching
import dev.aleksrychkov.geoghost.core.network.mapper.NetworkExceptionMapper.mapInternetException
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException

suspend inline fun <reified T> Request.executeForResult(): Result<T> {
    val client = OkHttpClientProvider.instance.client()
    val json = OkHttpClientProvider.instance.json()
    return suspendCancellableCoroutine { continuation ->
        val call = client.newCall(this)

        continuation.invokeOnCancellation {
            call.cancel()
        }

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isCancelled) return
                val exception = e.mapInternetException()
                continuation.resume(Result.failure(exception)) {
                    call.cancel()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val onCancellation = { _: Throwable -> response.closeQuietly() }
                safeRunCatching {
                    val body = response.body?.string() ?: ""
                    response.closeQuietly()

                    json.decodeFromString<T>(body)
                }
                    .onFailure { exception ->
                        if (!continuation.isCancelled) {
                            continuation.resume(
                                Result.failure(exception.mapInternetException()),
                                onCancellation
                            )
                        }
                    }
                    .onSuccess {
                        if (!continuation.isCancelled) {
                            continuation.resume(Result.success(it), onCancellation)
                        }
                    }
            }
        })
    }
}
