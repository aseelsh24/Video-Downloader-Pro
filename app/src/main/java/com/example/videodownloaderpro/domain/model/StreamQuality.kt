package com.example.videodownloaderpro.domain.model

data class StreamQuality(
    val url: String,
    val qualityLabel: String, // e.g., "1080p", "720p", "128kbps"
    val container: String, // e.g., "mp4", "webm", "mp3"
    val fileSize: Long?, // in bytes, if available
    val isAudioOnly: Boolean
)
