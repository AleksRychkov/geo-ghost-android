package dev.aleksrychkov.geoghost.core.exception

open class NoStackTraceException(msg: String? = null) : Exception(msg) {
    override fun fillInStackTrace(): Throwable {
        setStackTrace(emptyArray<StackTraceElement>())
        return this
    }
}
