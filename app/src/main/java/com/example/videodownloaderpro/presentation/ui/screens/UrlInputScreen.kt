package com.example.videodownloaderpro.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.videodownloaderpro.domain.model.VideoInfo
import com.example.videodownloaderpro.presentation.viewmodel.MainUiState

@Composable
fun UrlInputScreen(
    uiState: MainUiState,
    onUrlChanged: (String) -> Unit,
    onGetVideoInfoClicked: (String) -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToPreview: (VideoInfo) -> Unit
) {
    var url by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.videoInfo) {
        uiState.videoInfo?.let {
            onNavigateToPreview(it)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(message = it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Video Downloader Pro") },
                actions = {
                    Button(onClick = onNavigateToDownloads) {
                        Text("Downloads")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Enter Video URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onGetVideoInfoClicked(url) },
                enabled = !uiState.isLoading && url.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Video Info")
            }

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}
