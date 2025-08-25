package com.example.videodownloaderpro.domain.model

data class VideoInfo(
    val id: String,
    val title: String,
    val author: String,
    val durationSeconds: Long,
    val thumbnailUrl: String,
    val streams: List<StreamQuality>
)
