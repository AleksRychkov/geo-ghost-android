package dev.aleksrychkov.geoghost.feature.locationghost.provider

import android.os.SystemClock

internal fun TimeProvider(): TimeProvider = TimeProviderImpl()

internal interface TimeProvider {
    fun currentTimeInMillis(): Long
    fun elapsedRealtimeNanos(): Long
}

private class TimeProviderImpl : TimeProvider {
    override fun currentTimeInMillis(): Long = System.currentTimeMillis()
    override fun elapsedRealtimeNanos(): Long = SystemClock.elapsedRealtimeNanos()
}