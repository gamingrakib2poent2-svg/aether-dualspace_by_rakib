package com.aether.dualspace.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.dualspace.engine.CameraMode
import com.aether.dualspace.engine.VirtualCameraManager
import com.aether.dualspace.ui.components.GlassCard
import com.aether.dualspace.ui.components.GlassSwitch
import com.aether.dualspace.ui.theme.*

@Composable
fun CameraScreen(
    virtualCameraManager: VirtualCameraManager,
    onPickMediaForVirtualFeed: () -> Unit
) {
    val state by virtualCameraManager.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Virtual Camera Studio",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Camera access control & video stream injector",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Virtual Feed Viewport / Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black.copy(alpha = 0.85f))
                .border(1.dp, GlassBorderLight, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (state.mode) {
                CameraMode.HARDWARE -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = iOSGreen,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Live Hardware Camera Preview", color = Color.White, fontSize = 16.sp)
                        Text("Standard un-spoofed sensor output", color = TextSecondary, fontSize = 12.sp)
                    }
                }
                CameraMode.VIRTUAL_FEED -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(iOSPurple.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = iOSPurple,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Virtual Video Stream Active", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (state.activeFeedUri != null) "Feeding: ${state.activeFeedUri?.lastPathSegment}" else "Default Mock Loop Stream",
                            color = iOSPurple,
                            fontSize = 13.sp
                        )
                    }
                }
                CameraMode.PRIVACY_SHIELD -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = iOSRed,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Camera Shield Active", color = Color.White, fontSize = 16.sp)
                        Text("Output blocked (Zero light / Blackout)", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            // HUD Overlay Chip
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(state.spoofResolution, color = Color.White, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode Switcher Tabs
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CameraMode.values().forEach { mode ->
                    val isSelected = state.mode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) iOSPurple.copy(alpha = 0.7f) else Color.Transparent)
                            .clickable { virtualCameraManager.setCameraMode(mode) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = when (mode) {
                                CameraMode.HARDWARE -> "Real Cam"
                                CameraMode.VIRTUAL_FEED -> "Virtual Feed"
                                CameraMode.PRIVACY_SHIELD -> "Privacy Shield"
                            },
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Controls
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto Loop Injected Video", color = Color.White, fontSize = 14.sp)
                    GlassSwitch(
                        checked = state.isLooping,
                        onCheckedChange = { virtualCameraManager.toggleLooping(it) },
                        activeColor = iOSBlue
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sensor Grain / Realistic Noise", color = Color.White, fontSize = 14.sp)
                    GlassSwitch(
                        checked = state.noiseEffectEnabled,
                        onCheckedChange = { virtualCameraManager.toggleNoise(it) },
                        activeColor = iOSBlue
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(iOSPurple.copy(alpha = 0.25f))
                        .clickable { onPickMediaForVirtualFeed() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Choose Video / Photo for Virtual Cam",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
