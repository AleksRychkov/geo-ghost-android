package dev.aleksrychkov.geoghost.feature.permission.ui.mock

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel

internal class RequestMockLocationViewModel(
    private val dismiss: () -> Unit
) : ViewModel() {

    private companion object {
        const val TAG = "RequestMockLocationViewModel"
        const val ACTION = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS"
    }

    fun navigateToDeveloperOptions(context: Context) {
        dismiss()
        try {
            Intent(ACTION).let(context::startActivity)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open developer options", e)
            Toast.makeText(
                context,
                "Failed to open developer options. Check if developer options are enabled on your device",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}