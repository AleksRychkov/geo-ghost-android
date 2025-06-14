package dev.aleksrychkov.geoghost.feature.permission.handler

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import dev.aleksrychkov.geoghost.feature.permission.handler.utils.ActivityLifecycleCallbacksAdapter
import dev.aleksrychkov.geoghost.feature.permission.handler.utils.isCurrentApplication
import java.util.WeakHashMap

internal class RuntimePermissionActivityHandler(
    context: Context,
    private val packageName: String = context.packageName,
) : ActivityLifecycleCallbacksAdapter {

    private val managers = WeakHashMap<Activity, RuntimePermissionManager>()
    private var activityCounter: Int = 0

    init {
        (context as Application).registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (!activity.isCurrentApplication(packageName)) return
        val componentActivity = (activity as? ComponentActivity) ?: return
        val manager = RuntimePermissionManager(componentActivity)
        managers[activity] = manager

        if (RuntimePermissionStatus.instance == null) {
            RuntimePermissionStatus.instance = manager
        }
        if (RuntimePermissionRequester.instance == null) {
            RuntimePermissionRequester.instance = manager
        }
        activityCounter++
    }

    override fun onActivityResumed(activity: Activity) {
        if (!activity.isCurrentApplication(packageName)) return
        val manager = managers[activity] ?: return
        if (RuntimePermissionStatus.instance != manager) {
            RuntimePermissionStatus.instance = manager
        }
        if (RuntimePermissionRequester.instance != manager) {
            RuntimePermissionRequester.instance = manager
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (!activity.isCurrentApplication(packageName)) return
        managers[activity]?.unregister()
        managers.remove(activity)
        activityCounter--
        if (activityCounter == 0) {
            RuntimePermissionStatus.instance = null
            RuntimePermissionRequester.instance = null
        }
    }
}
