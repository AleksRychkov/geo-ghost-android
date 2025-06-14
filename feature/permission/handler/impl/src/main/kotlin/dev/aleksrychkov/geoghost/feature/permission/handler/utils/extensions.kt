package dev.aleksrychkov.geoghost.feature.permission.handler.utils

import android.content.Context
import android.os.Looper

internal fun Context.isCurrentApplication(packageName: String) =
    this.packageName == packageName

internal fun checkMainThread() {
    if (Thread.currentThread() !== Looper.getMainLooper().thread) {
        throw IllegalStateException("Permission request must be done only from main thread!")
    }
}
