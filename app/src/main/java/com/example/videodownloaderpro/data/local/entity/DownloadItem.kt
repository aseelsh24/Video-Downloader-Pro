package com.example.videodownloaderpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELED
}

@Entity(tableName = "downloads")
data class DownloadItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val downloadUrl: String,
    val filePath: String?, // Path on local storage once completed
    val qualityLabel: String,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val progress: Int = 0, // 0-100
    val totalSize: Long, // in bytes
    val downloadedSize: Long = 0, // in bytes
    val createdAt: Long = System.currentTimeMillis()
)
