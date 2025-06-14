package dev.aleksrychkov.geoghost.feature.locationghost.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import dev.aleksrychkov.geoghost.feature.locationghost.data.LocationGhostStorage
import dev.aleksrychkov.geoghost.feature.locationghost.provider.LocationGhostProvider
import dev.aleksrychkov.geoghost.feature.locationghost.provider.LocationGhostProviderResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

internal class LocationGhostService : Service() {

    private companion object {
        const val TAG = "LocationGhostService"
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false
    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO.limitedParallelism(1) + SupervisorJob())
    }

    // will not use it
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        val notification = LocationGhostServiceNotificationManager.instance.createNotification()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ${intent?.action}")
        if (intent != null) {
            val action = intent.action
            when (action) {
                LocationGhostServiceActions.START.name -> startService()
                LocationGhostServiceActions.STOP.name -> stopService()
                else -> throw IllegalStateException("Cannot start service withou action")
            }
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "onTaskRemoved")
        scope.launch(NonCancellable) {
            val isServiceRunning = LocationGhostStorage.instance.inServiceRunning()
            if (!isServiceRunning) return@launch

            val restartServiceIntent =
                Intent(applicationContext, LocationGhostService::class.java).also {
                    it.action = LocationGhostServiceActions.START.name
                    it.setPackage(packageName)
                }
            val restartServicePendingIntent: PendingIntent =
                PendingIntent.getService(
                    this@LocationGhostService, 1, restartServiceIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            applicationContext.getSystemService(Context.ALARM_SERVICE)
            val alarmService: AlarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        scope.coroutineContext.cancelChildren()
    }

    @SuppressLint("WakelockTimeout")
    private fun startService() {
        if (isServiceStarted) return
        isServiceStarted = true
        scope.launch {
            LocationGhostStorage.instance.setServiceRunning(true)
        }
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationGhostService::lock").apply {
                acquire()
            }
        }

        val ghostProviderResult = LocationGhostProvider.instance.start()
        if (ghostProviderResult != LocationGhostProviderResult.SUCCESS) {
            stopService()
            val errorMsg = "Failed to start service." +
                    " LocationGhostProvider has not started: $ghostProviderResult"
            Log.e(TAG, errorMsg)
        }
    }

    private fun stopService() {
        LocationGhostProvider.instance.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isServiceStarted = false
        scope.launch(NonCancellable) {
            LocationGhostStorage.instance.setServiceRunning(false)
        }
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            wakeLock = null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release WakeLock", e)
        }
    }
}
