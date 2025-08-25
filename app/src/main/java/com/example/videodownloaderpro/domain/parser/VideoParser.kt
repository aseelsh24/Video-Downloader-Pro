package com.example.videodownloaderpro.domain.parser

import com.example.videodownloaderpro.domain.model.VideoInfo

interface VideoParser {
    /**
     * Checks if this parser can handle the given URL.
     * @param url The video URL.
     * @return True if the parser supports the URL, false otherwise.
     */
    fun canParse(url: String): Boolean

    /**
     * Parses the given URL to extract video information.
     * This should be called from a background thread.
     * @param url The video URL.
     * @return A [VideoInfo] object containing the parsed data.
     * @throws Exception if parsing fails.
     */
    suspend fun parse(url: String): VideoInfo
}
