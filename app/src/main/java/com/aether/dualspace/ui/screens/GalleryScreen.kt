package com.aether.dualspace.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.aether.dualspace.engine.*
import com.aether.dualspace.ui.components.GlassCard
import com.aether.dualspace.ui.theme.*

@Composable
fun GalleryScreen(
    vaultManager: VaultStorageManager,
    virtualCameraManager: VirtualCameraManager,
    onPlayMedia: (Uri, String, Boolean) -> Unit
) {
    var selectedTab by remember { mutableStateOf(MediaType.PHOTO) }
    var mediaList by remember { mutableStateOf(vaultManager.getMediaByType(selectedTab)) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            vaultManager.importMedia(uri, selectedTab, "Vault_Item_${System.currentTimeMillis()}")
            mediaList = vaultManager.getMediaByType(selectedTab)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Vault Gallery",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Encrypted & hidden from phone gallery",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iOSBlue.copy(alpha = 0.8f))
                    .clickable {
                        val mime = when (selectedTab) {
                            MediaType.PHOTO -> "image/*"
                            MediaType.VIDEO -> "video/*"
                            MediaType.AUDIO -> "audio/*"
                        }
                        filePicker.launch(mime)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Import", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(MediaType.PHOTO, MediaType.VIDEO, MediaType.AUDIO).forEach { type ->
                    val isSelected = selectedTab == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) iOSBlue.copy(alpha = 0.7f) else Color.Transparent)
                            .clickable {
                                selectedTab = type
                                mediaList = vaultManager.getMediaByType(type)
                            }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = when (type) {
                                MediaType.PHOTO -> "Photos"
                                MediaType.VIDEO -> "Videos"
                                MediaType.AUDIO -> "Audios"
                            },
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mediaList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Vault is empty", color = TextSecondary, fontSize = 15.sp)
                    Text("Tap + above to import files safely", color = TextTertiary, fontSize = 12.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mediaList) { item ->
                    GlassCard(
                        modifier = Modifier
                            .aspectRatio(0.9f)
                            .clickable {
                                onPlayMedia(
                                    Uri.fromFile(item.file),
                                    item.name,
                                    item.type == MediaType.VIDEO
                                )
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item.type) {
                                        MediaType.PHOTO -> Icons.Default.Image
                                        MediaType.VIDEO -> Icons.Default.PlayCircle
                                        MediaType.AUDIO -> Icons.Default.MusicNote
                                    },
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                maxLines = 1
                            )

                            // Quick Action: Inject as Virtual Camera
                            if (item.type == MediaType.VIDEO || item.type == MediaType.PHOTO) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(iOSPurple.copy(alpha = 0.35f))
                                        .clickable {
                                            virtualCameraManager.setVirtualFeed(Uri.fromFile(item.file))
                                        }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Set as Virtual Cam", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
