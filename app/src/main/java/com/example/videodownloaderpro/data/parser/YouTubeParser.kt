package com.example.videodownloaderpro.data.parser

import com.example.videodownloaderpro.domain.model.StreamQuality
import com.example.videodownloaderpro.domain.model.VideoInfo
import com.example.videodownloaderpro.domain.parser.VideoParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.regex.Pattern
import javax.inject.Inject

class YouTubeParser @Inject constructor() : VideoParser {

    companion object {
        private const val YOUTUBE_URL_PATTERN = "(?:https?://)?(?:www\\.)?(?:youtube\\.com|youtu\\.be)/.+"
    }

    override fun canParse(url: String): Boolean {
        return url.matches(Regex(YOUTUBE_URL_PATTERN))
    }

    override suspend fun parse(url: String): VideoInfo = withContext(Dispatchers.IO) {
        if (!canParse(url)) {
            throw IllegalArgumentException("Invalid YouTube URL")
        }

        // --- IMPORTANT DISCLAIMER ---
        // Parsing YouTube is extremely complex. It requires reverse-engineering obfuscated
        // JavaScript that generates the download URLs (the 'cipher'). This changes frequently.
        // A robust solution often requires a server-side component or a dedicated library
        // like yt-dlp that is constantly updated.
        //
        // The following is a simplified placeholder for demonstration purposes.
        // It only extracts the title and returns hardcoded stream data.

        val doc = Jsoup.connect(url).get()
        val title = doc.title().replace(" - YouTube", "").trim()
        val videoId = extractVideoId(url)

        // Placeholder stream data
        val streams = listOf(
            StreamQuality(
                url = "https://example.com/video_1080.mp4",
                qualityLabel = "1080p",
                container = "mp4",
                fileSize = 50 * 1024 * 1024, // 50 MB
                isAudioOnly = false
            ),
            StreamQuality(
                url = "https://example.com/video_720.mp4",
                qualityLabel = "720p",
                container = "mp4",
                fileSize = 25 * 1024 * 1024, // 25 MB
                isAudioOnly = false
            ),
            StreamQuality(
                url = "https://example.com/audio_128.mp3",
                qualityLabel = "128kbps",
                container = "mp3",
                fileSize = 4 * 1024 * 1024, // 4 MB
                isAudioOnly = true
            )
        )

        VideoInfo(
            id = videoId ?: "unknown",
            title = title.ifBlank { "Unknown Title" },
            author = "Unknown Author", // This is also hard to get reliably
            durationSeconds = 300, // Placeholder duration
            thumbnailUrl = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg",
            streams = streams
        )
    }

    private fun extractVideoId(url: String): String? {
        val pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(url)
        return if (matcher.find()) matcher.group() else null
    }
}
