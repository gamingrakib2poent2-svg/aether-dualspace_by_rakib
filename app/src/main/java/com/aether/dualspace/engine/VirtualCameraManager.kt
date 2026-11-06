package com.aether.dualspace.engine

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CameraMode {
    HARDWARE,       // Real phone camera
    VIRTUAL_FEED,   // Injected custom video/photo stream
    PRIVACY_SHIELD  // Blocked / Camouflage black feed
}

data class VirtualCameraState(
    val mode: CameraMode = CameraMode.VIRTUAL_FEED,
    val isVirtualCameraActive: Boolean = true,
    val activeFeedUri: Uri? = null,
    val isLooping: Boolean = true,
    val feedFps: Int = 30,
    val spoofResolution: String = "1080p (1920x1080)",
    val noiseEffectEnabled: Boolean = false,
    val privacyStatusText: String = "Virtual Camera Active"
)

class VirtualCameraManager private constructor(private val context: Context) {

    private val _state = MutableStateFlow(VirtualCameraState())
    val state: StateFlow<VirtualCameraState> = _state.asStateFlow()

    fun setCameraMode(newMode: CameraMode) {
        val status = when (newMode) {
            CameraMode.HARDWARE -> "Hardware Camera Active"
            CameraMode.VIRTUAL_FEED -> "Virtual Feed Injector Active"
            CameraMode.PRIVACY_SHIELD -> "Camera Hardware Blocked"
        }
        _state.value = _state.value.copy(
            mode = newMode,
            privacyStatusText = status,
            isVirtualCameraActive = (newMode == CameraMode.VIRTUAL_FEED)
        )
    }

    fun setVirtualFeed(uri: Uri?) {
        _state.value = _state.value.copy(
            activeFeedUri = uri,
            mode = CameraMode.VIRTUAL_FEED,
            isVirtualCameraActive = true,
            privacyStatusText = "Custom Feed Injected: ${uri?.lastPathSegment ?: "Default Loop"}"
        )
    }

    fun toggleLooping(loop: Boolean) {
        _state.value = _state.value.copy(isLooping = loop)
    }

    fun toggleNoise(enabled: Boolean) {
        _state.value = _state.value.copy(noiseEffectEnabled = enabled)
    }

    companion object {
        @Volatile
        private var instance: VirtualCameraManager? = null

        fun getInstance(context: Context): VirtualCameraManager {
            return instance ?: synchronized(this) {
                instance ?: VirtualCameraManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
