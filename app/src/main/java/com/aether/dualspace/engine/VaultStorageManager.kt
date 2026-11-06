package com.aether.dualspace.engine

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

enum class MediaType {
    PHOTO, VIDEO, AUDIO
}

data class VaultMediaItem(
    val id: String,
    val name: String,
    val file: File,
    val type: MediaType,
    val sizeBytes: Long,
    val dateAdded: Long = System.currentTimeMillis()
)

class VaultStorageManager(private val context: Context) {

    private val vaultDir: File = File(context.filesDir, "secure_vault").apply {
        if (!exists()) mkdirs()
    }

    private val photosDir = File(vaultDir, "photos").apply { if (!exists()) mkdirs() }
    private val videosDir = File(vaultDir, "videos").apply { if (!exists()) mkdirs() }
    private val audiosDir = File(vaultDir, "audios").apply { if (!exists()) mkdirs() }

    fun importMedia(uri: Uri, type: MediaType, fileName: String): VaultMediaItem? {
        return try {
            val targetDir = when (type) {
                MediaType.PHOTO -> photosDir
                MediaType.VIDEO -> videosDir
                MediaType.AUDIO -> audiosDir
            }
            val targetFile = File(targetDir, "${System.currentTimeMillis()}_$fileName")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            VaultMediaItem(
                id = targetFile.name,
                name = fileName,
                file = targetFile,
                type = type,
                sizeBytes = targetFile.length()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAllMedia(): List<VaultMediaItem> {
        val list = mutableListOf<VaultMediaItem>()
        photosDir.listFiles()?.forEach { file ->
            list.add(VaultMediaItem(file.name, file.name, file, MediaType.PHOTO, file.length(), file.lastModified()))
        }
        videosDir.listFiles()?.forEach { file ->
            list.add(VaultMediaItem(file.name, file.name, file, MediaType.VIDEO, file.length(), file.lastModified()))
        }
        audiosDir.listFiles()?.forEach { file ->
            list.add(VaultMediaItem(file.name, file.name, file, MediaType.AUDIO, file.length(), file.lastModified()))
        }
        return list.sortedByDescending { it.dateAdded }
    }

    fun getMediaByType(type: MediaType): List<VaultMediaItem> {
        return getAllMedia().filter { it.type == type }
    }

    fun deleteItem(item: VaultMediaItem): Boolean {
        return item.file.delete()
    }
}
