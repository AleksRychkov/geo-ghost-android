package dev.aleksrychkov.geoghost.core.network

import android.content.Context
import dev.aleksrychkov.geoghost.core.buildconfig.BuildConfigProxy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

interface OkHttpClientProvider {
    companion object {
        lateinit var instance: OkHttpClientProvider
        internal fun initialize(context: Context) {
            instance = OkHttpClientProviderImpl(context = context, dispatcher = Dispatchers.IO)
        }
    }

    suspend fun client(): OkHttpClient
    suspend fun json(): Json
}

private class OkHttpClientProviderImpl(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher
) : OkHttpClientProvider {

    private companion object {
        const val TIMEOUT_MINUTES: Long = 2
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = if (BuildConfigProxy.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .cache(
                Cache(
                    directory = File(context.cacheDir, "http_cache"),
                    maxSize = 50L * 1024L * 1024L // 50 MiB
                )
            )
            .addInterceptor(logger)
            .connectTimeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
            .writeTimeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
            .readTimeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
            .callTimeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
            .build()
    }

    private val json: Json by lazy {
        Json { ignoreUnknownKeys = true }
    }

    override suspend fun client(): OkHttpClient {
        return withContext(dispatcher) {
            okHttpClient
        }
    }

    override suspend fun json(): Json {
        return json
    }
}
