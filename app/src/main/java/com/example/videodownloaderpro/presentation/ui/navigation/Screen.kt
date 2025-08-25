package com.example.videodownloaderpro.presentation.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object UrlInput : Screen("url_input")
    object VideoPreview : Screen("video_preview") // We will pass data via ViewModel, not nav args
    object Downloads : Screen("downloads")

    object Player : Screen("player/{videoUri}") {
        fun createRoute(videoUri: String): String {
            val encodedUri = URLEncoder.encode(videoUri, StandardCharsets.UTF_8.toString())
            return "player/$encodedUri"
        }
    }
}
