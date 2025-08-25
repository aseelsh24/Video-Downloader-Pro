package com.example.videodownloaderpro.presentation.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.videodownloaderpro.presentation.ui.components.DownloadListItem
import com.example.videodownloaderpro.presentation.viewmodel.DownloadsViewModel

@Composable
fun DownloadListScreen(
    viewModel: DownloadsViewModel = hiltViewModel(),
    onNavigateToPlayer: (String) -> Unit,
    onBack: () -> Unit
) {
    val downloads by viewModel.downloads.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Downloads") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {
            items(downloads) { item ->
                DownloadListItem(
                    item = item,
                    onItemClicked = {
                        item.filePath?.let { onNavigateToPlayer(it) }
                    }
                )
            }
        }
    }
}
