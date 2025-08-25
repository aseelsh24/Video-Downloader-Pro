package com.example.videodownloaderpro.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.videodownloaderpro.domain.model.StreamQuality
import com.example.videodownloaderpro.domain.model.VideoInfo

@Composable
fun VideoPreviewScreen(
    videoInfo: VideoInfo,
    onStartDownload: (url: String, qualityLabel: String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // In a real app, use Icons.Auto.Filled.ArrowBack
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
                .padding(16.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = videoInfo.thumbnailUrl,
                        contentDescription = "Video Thumbnail",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = videoInfo.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "by ${videoInfo.author}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Available Qualities",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            items(videoInfo.streams) { stream ->
                StreamQualityItem(
                    stream = stream,
                    onClicked = {
                        onStartDownload(stream.url, stream.qualityLabel)
                    }
                )
            }
        }
    }
}

@Composable
private fun StreamQualityItem(
    stream: StreamQuality,
    onClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClicked),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stream.qualityLabel,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stream.container.uppercase()} - ${if (stream.isAudioOnly) "Audio" else "Video"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(onClick = onClicked) {
                Text("Download")
            }
        }
    }
}
