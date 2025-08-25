package com.example.videodownloaderpro.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.videodownloaderpro.presentation.ui.screens.DownloadListScreen
import com.example.videodownloaderpro.presentation.ui.screens.PlayerScreen
import com.example.videodownloaderpro.presentation.ui.screens.UrlInputScreen
import com.example.videodownloaderpro.presentation.ui.screens.VideoPreviewScreen
import com.example.videodownloaderpro.presentation.viewmodel.MainViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // Share MainViewModel between UrlInput and VideoPreview
    val mainViewModel: MainViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Screen.UrlInput.route) {
        composable(Screen.UrlInput.route) {
            val uiState by mainViewModel.uiState.collectAsState()
            UrlInputScreen(
                uiState = uiState,
                onUrlChanged = mainViewModel::onUrlChanged,
                onGetVideoInfoClicked = mainViewModel::getVideoInfo,
                onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) },
                onNavigateToPreview = {
                    // ViewModel holds the info, just navigate
                    navController.navigate(Screen.VideoPreview.route)
                }
            )
        }
        composable(Screen.VideoPreview.route) {
            val uiState by mainViewModel.uiState.collectAsState()
            uiState.videoInfo?.let { videoInfo ->
                VideoPreviewScreen(
                    videoInfo = videoInfo,
                    onStartDownload = { url, quality ->
                        mainViewModel.startDownload(videoInfo, url, quality)
                        // Navigate to downloads screen after starting
                        navController.navigate(Screen.Downloads.route) {
                           popUpTo(Screen.UrlInput.route) // Clear back stack
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.Downloads.route) {
            DownloadListScreen(
                onNavigateToPlayer = { uri ->
                    navController.navigate(Screen.Player.createRoute(uri))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("videoUri") { type = NavType.StringType })
        ) {
            PlayerScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
