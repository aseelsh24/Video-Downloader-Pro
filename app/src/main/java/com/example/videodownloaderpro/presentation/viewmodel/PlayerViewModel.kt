package com.example.videodownloaderpro.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class PlayerUiState(
    val videoUri: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Retrieve the video URI from navigation arguments
        val videoUri = savedStateHandle.get<String>("videoUri")
        _uiState.value = PlayerUiState(videoUri = videoUri)
    }
}
