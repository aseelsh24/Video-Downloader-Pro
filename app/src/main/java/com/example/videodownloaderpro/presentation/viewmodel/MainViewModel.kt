package com.example.videodownloaderpro.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videodownloaderpro.data.downloader.DownloadService
import com.example.videodownloaderpro.data.local.entity.DownloadItem
import com.example.videodownloaderpro.data.repository.DownloadRepository
import com.example.videodownloaderpro.domain.model.VideoInfo
import com.example.videodownloaderpro.domain.usecase.GetVideoInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = false,
    val videoInfo: VideoInfo? = null,
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getVideoInfoUseCase: GetVideoInfoUseCase,
    private val downloadRepository: DownloadRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun onUrlChanged(url: String) {
        // Can add validation or other logic here
    }

    fun getVideoInfo(url: String) {
        viewModelScope.launch {
            _uiState.value = MainUiState(isLoading = true)
            val result = getVideoInfoUseCase(url)
            _uiState.value = result.fold(
                onSuccess = { MainUiState(videoInfo = it) },
                onFailure = { MainUiState(error = it.message ?: "An unknown error occurred") }
            )
        }
    }

    fun startDownload(videoInfo: VideoInfo, qualityUrl: String, qualityLabel: String) {
        viewModelScope.launch {
            val downloadItem = DownloadItem(
                videoId = videoInfo.id,
                title = videoInfo.title,
                thumbnailUrl = videoInfo.thumbnailUrl,
                downloadUrl = qualityUrl,
                filePath = null,
                qualityLabel = qualityLabel,
                totalSize = 0 // Will be updated by service
            )
            val newId = downloadRepository.addDownload(downloadItem)

            val intent = Intent(context, DownloadService::class.java).apply {
                action = DownloadService.ACTION_START_DOWNLOAD
                putExtra(DownloadService.EXTRA_DOWNLOAD_ID, newId)
            }
            context.startService(intent)
        }
    }
}
