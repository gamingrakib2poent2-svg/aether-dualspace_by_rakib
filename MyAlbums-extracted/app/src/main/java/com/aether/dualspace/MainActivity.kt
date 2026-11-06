package com.aether.dualspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.aether.dualspace.engine.*
import com.aether.dualspace.ui.screens.*
import com.aether.dualspace.ui.theme.*

enum class AppDestination(val icon: ImageVector, val label: String) {
    HOME(Icons.Default.GridView, "Home"),
    CAMERA(Icons.Default.Videocam, "Camera"),
    GALLERY(Icons.Default.PhotoLibrary, "Gallery"),
    PLAYER(Icons.Default.PlayCircle, "Player")
}

class MainActivity : ComponentActivity() {

    private lateinit var virtualCameraManager: VirtualCameraManager
    private lateinit var vaultStorageManager: VaultStorageManager
    private lateinit var mediaPlaybackManager: MediaPlaybackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        virtualCameraManager = VirtualCameraManager.getInstance(this)
        vaultStorageManager = VaultStorageManager(this)
        mediaPlaybackManager = MediaPlaybackManager(this)

        setContent {
            AetherDualSpaceTheme {
                var currentTab by remember { mutableStateOf(AppDestination.HOME) }
                var activeClonedUrl by remember { mutableStateOf<String?>(null) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(MeshGradientAccent, MeshGradient1, MeshGradient2),
                                radius = 1800f
                            )
                        )
                ) {
                    if (activeClonedUrl != null) {
                        AppClonerScreen(
                            targetUrl = activeClonedUrl!!,
                            onBack = { activeClonedUrl = null }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (currentTab) {
                                AppDestination.HOME -> HomeScreen(
                                    virtualCameraManager = virtualCameraManager,
                                    onNavigateToCamera = { currentTab = AppDestination.CAMERA },
                                    onNavigateToGallery = { currentTab = AppDestination.GALLERY },
                                    onNavigateToCloner = { url -> activeClonedUrl = url }
                                )
                                AppDestination.CAMERA -> CameraScreen(
                                    virtualCameraManager = virtualCameraManager,
                                    onPickMediaForVirtualFeed = { currentTab = AppDestination.GALLERY }
                                )
                                AppDestination.GALLERY -> GalleryScreen(
                                    vaultManager = vaultStorageManager,
                                    virtualCameraManager = virtualCameraManager,
                                    onPlayMedia = { uri, title, isVideo ->
                                        mediaPlaybackManager.playMedia(uri, title, isVideo)
                                        currentTab = AppDestination.PLAYER
                                    }
                                )
                                AppDestination.PLAYER -> MediaPlayerScreen(
                                    mediaPlaybackManager = mediaPlaybackManager
                                )
                            }

                            // iOS Floating Frosted Glass Bottom Dock
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 20.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(Color(0x33FFFFFF))
                                    .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(32.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppDestination.values().forEach { destination ->
                                        val isSelected = currentTab == destination
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) iOSBlue else Color.Transparent
                                                )
                                                .clickable { currentTab = destination },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = destination.icon,
                                                contentDescription = destination.label,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlaybackManager.release()
    }
}
