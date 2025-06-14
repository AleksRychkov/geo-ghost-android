package dev.aleksrychkov.geoghost.feature.permission.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aleksrychkov.geoghost.feature.permission.handler.RuntimePermissionRequester
import dev.aleksrychkov.geoghost.feature.permission.handler.RuntimePermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal abstract class AbstractPermissionViewModel(
    protected val closeSelf: () -> Unit,
) : ViewModel(), DefaultLifecycleObserver {

    private val _state = MutableStateFlow(PermissionState())
    val state = _state.asStateFlow()

    private val permissionStatus: RuntimePermissionStatus
        get() = requireNotNull(RuntimePermissionStatus.instance)

    protected val permissionRequester: RuntimePermissionRequester
        get() = requireNotNull(RuntimePermissionRequester.instance)

    override fun onStart(owner: LifecycleOwner) {
        viewModelScope.launch {
            updateCurrentState()
        }
    }

    fun requestPermission(context: Context) {
        if (_state.value.shouldRequestSettings) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
            val msg = settingsNavigateToMessage(context)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            val granted = request(context)
            if (granted) {
                _state.emit(
                    _state.value.copy(
                        isLoading = false,
                        granted = true,
                        shouldShowRationale = false,
                        shouldRequestSettings = false,
                    )
                )
                closeSelf()
            } else {
                updateCurrentState()
            }
        }
    }

    protected abstract fun mainPermission(): String
    protected abstract fun settingsNavigateToMessage(context: Context): String
    protected abstract suspend fun request(context: Context): Boolean


    private suspend fun updateCurrentState() {
        val result = permissionStatus.getStatus(mainPermission())
        if (result.granted) {
            closeSelf()
        } else {
            _state.emit(
                _state.value.copy(
                    isLoading = false,
                    granted = false,
                    shouldShowRationale = result.shouldShowRationale,
                    shouldRequestSettings = result.shouldRequestSettings,
                )
            )
        }
    }
}
