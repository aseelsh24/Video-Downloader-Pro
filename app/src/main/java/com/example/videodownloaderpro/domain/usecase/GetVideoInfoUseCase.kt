package com.example.videodownloaderpro.domain.usecase

import com.example.videodownloaderpro.domain.model.VideoInfo
import com.example.videodownloaderpro.domain.parser.VideoParser
import javax.inject.Inject

class GetVideoInfoUseCase @Inject constructor(
    private val parsers: Set<@JvmSuppressWildcards VideoParser>
) {
    suspend operator fun invoke(url: String): Result<VideoInfo> {
        return try {
            val parser = parsers.firstOrNull { it.canParse(url) }
                ?: throw IllegalArgumentException("No parser found for this URL.")
            val videoInfo = parser.parse(url)
            Result.success(videoInfo)
        } catch (e: Exception) {
            // Log the exception e
            Result.failure(e)
        }
    }
}
