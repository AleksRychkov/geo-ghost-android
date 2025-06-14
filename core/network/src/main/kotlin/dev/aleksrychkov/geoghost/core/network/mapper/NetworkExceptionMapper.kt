package dev.aleksrychkov.geoghost.core.network.mapper

import dev.aleksrychkov.geoghost.core.exception.NoInternetException
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkExceptionMapper {

    fun Throwable.mapInternetException(): Throwable {
        return if (isInternetException()) {
            NoInternetException()
        } else {
            this
        }
    }

    private fun Throwable.isInternetException(): Boolean {
        return when (this) {
            is UnknownHostException,
            is ConnectException,
            is SocketTimeoutException,
            is NoRouteToHostException,
            is IOException -> true

            else -> false
        }
    }
}
