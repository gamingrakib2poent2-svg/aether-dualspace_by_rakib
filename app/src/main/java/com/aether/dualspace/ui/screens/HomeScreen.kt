package com.aether.dualspace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.dualspace.engine.CameraMode
import com.aether.dualspace.engine.VirtualCameraManager
import com.aether.dualspace.ui.components.GlassCard
import com.aether.dualspace.ui.components.GlassSwitch
import com.aether.dualspace.ui.theme.*

data class ClonedAppItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val url: String
)

@Composable
fun HomeScreen(
    virtualCameraManager: VirtualCameraManager,
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToCloner: (String) -> Unit
) {
    val cameraState by virtualCameraManager.state.collectAsState()

    val clonedApps = remember {
        listOf(
            ClonedAppItem("wa", "WhatsApp Dual", Icons.Default.Chat, Color(0xFF25D366), "https://web.whatsapp.com"),
            ClonedAppItem("tg", "Telegram Space", Icons.Default.Send, Color(0xFF0088CC), "https://web.telegram.org"),
            ClonedAppItem("fb", "Messenger 2", Icons.Default.Forum, Color(0xFF0084FF), "https://m.facebook.com"),
            ClonedAppItem("ig", "Instagram Clone", Icons.Default.CameraAlt, Color(0xFFE1306C), "https://www.instagram.com"),
            ClonedAppItem("br", "Ghost Browser", Icons.Default.Language, iOSPurple, "https://duckduckgo.com"),
            ClonedAppItem("add", "Add App Clone", Icons.Default.Add, Color.White.copy(alpha = 0.5f), "")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Dynamic Island Header
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            if (cameraState.mode == CameraMode.VIRTUAL_FEED) iOSPurple else iOSGreen
                        )
                )
                Text(
                    text = if (cameraState.mode == CameraMode.VIRTUAL_FEED) "Virtual Cam Spoof Active" else "Dual Space Secured",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "Aether Space",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = "iOS 26 Liquid Glass Isolation Vault",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Virtual Camera Access Controller Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = GlassThin,
            borderColor = if (cameraState.mode == CameraMode.VIRTUAL_FEED) iOSPurple else GlassBorderLight
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(iOSPurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = iOSPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Virtual Camera Control",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = cameraState.privacyStatusText,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    GlassSwitch(
                        checked = cameraState.mode == CameraMode.VIRTUAL_FEED,
                        onCheckedChange = { isChecked ->
                            virtualCameraManager.setCameraMode(
                                if (isChecked) CameraMode.VIRTUAL_FEED else CameraMode.HARDWARE
                            )
                        },
                        activeColor = iOSPurple
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable { onNavigateToCamera() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Configure Feed", color = Color.White, fontSize = 13.sp)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable { onNavigateToGallery() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Pick Mock Video", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cloned Apps Section
        Text(
            text = "Cloned Workspace Apps",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Isolated storage and virtual camera feed bridged",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(260.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(clonedApps) { app ->
                GlassCard(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable {
                            if (app.id != "add") onNavigateToCloner(app.url)
                        },
                    cornerRadius = 20.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(app.color.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = app.icon,
                                contentDescription = app.name,
                                tint = app.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = app.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Vault Glance
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = GlassThin
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Secure Vault Gallery", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("Hidden from Android system gallery", color = TextSecondary, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(iOSBlue.copy(alpha = 0.3f))
                        .clickable { onNavigateToGallery() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Open Vault", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
