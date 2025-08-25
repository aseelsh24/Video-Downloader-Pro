package com.example.videodownloaderpro.data.repository

import com.example.videodownloaderpro.data.local.dao.DownloadDao
import com.example.videodownloaderpro.data.local.entity.DownloadItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    private val downloadDao: DownloadDao
) {

    fun getAllDownloads(): Flow<List<DownloadItem>> {
        return downloadDao.getAll()
    }

    suspend fun addDownload(downloadItem: DownloadItem): Long {
        return downloadDao.insert(downloadItem)
    }

    suspend fun updateDownload(downloadItem: DownloadItem) {
        downloadDao.update(downloadItem)
    }

    suspend fun deleteDownload(downloadItem: DownloadItem) {
        downloadDao.delete(downloadItem)
    }

    suspend fun getDownload(id: Long): DownloadItem? {
        return downloadDao.getById(id)
    }
}
