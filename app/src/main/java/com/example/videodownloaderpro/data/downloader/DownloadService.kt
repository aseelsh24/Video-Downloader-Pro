package com.example.videodownloaderpro.data.downloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.videodownloaderpro.R
import com.example.videodownloaderpro.data.local.entity.DownloadStatus
import com.example.videodownloaderpro.data.repository.DownloadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {

    @Inject
    lateinit var okHttpClient: OkHttpClient
    @Inject
    lateinit var downloadRepository: DownloadRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val downloadId = intent?.getLongExtra(EXTRA_DOWNLOAD_ID, -1L) ?: -1L

        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                if (downloadId != -1L) {
                    serviceScope.launch {
                        startDownload(downloadId)
                    }
                }
            }
            ACTION_CANCEL_DOWNLOAD -> {
                if (downloadId != -1L) {
                    // TODO: Implement cancellation logic
                }
                stopSelfResult(startId)
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun startDownload(downloadId: Long) {
        val downloadItem = downloadRepository.getDownload(downloadId) ?: return

        // Set status to Downloading
        downloadRepository.updateDownload(downloadItem.copy(status = DownloadStatus.DOWNLOADING))

        startForeground(NOTIFICATION_ID, createNotification(0, "Starting..."))

        val request = okhttp3.Request.Builder()
            .url(downloadItem.downloadUrl)
            .header("Range", "bytes=${downloadItem.downloadedSize}-")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) throw Exception("Unexpected code ${response.code}")

            val body = response.body ?: throw Exception("Response body is null")
            val contentLength = body.contentLength() + downloadItem.downloadedSize
            val inputStream = body.byteStream()

            val file = File(filesDir, "${downloadItem.videoId}.mp4")
            val outputStream = FileOutputStream(file, true) // Append mode

            var bytesCopied: Long = downloadItem.downloadedSize
            val buffer = ByteArray(8 * 1024)
            var bytes = inputStream.read(buffer)
            var lastUpdateTime = System.currentTimeMillis()

            while (bytes >= 0) {
                outputStream.write(buffer, 0, bytes)
                bytesCopied += bytes
                bytes = inputStream.read(buffer)

                // Update progress every second or so to avoid excessive DB writes/UI updates
                if (System.currentTimeMillis() - lastUpdateTime > 1000) {
                    val progress = ((bytesCopied * 100) / contentLength).toInt()
                    downloadRepository.updateDownload(downloadItem.copy(
                        progress = progress,
                        downloadedSize = bytesCopied,
                        totalSize = contentLength
                    ))
                    notificationManager.notify(NOTIFICATION_ID, createNotification(progress, "$progress%"))
                    lastUpdateTime = System.currentTimeMillis()
                }
            }

            outputStream.close()
            inputStream.close()

            // Download finished
            downloadRepository.updateDownload(downloadItem.copy(
                status = DownloadStatus.COMPLETED,
                progress = 100,
                filePath = file.absolutePath
            ))
            notificationManager.notify(NOTIFICATION_ID, createNotification(100, "Download complete"))

        } catch (e: Exception) {
            e.printStackTrace()
            downloadRepository.updateDownload(downloadItem.copy(status = DownloadStatus.FAILED))
            notificationManager.notify(NOTIFICATION_ID, createNotification(0, "Download failed"))
        } finally {
            stopForeground(STOP_FOREGROUND_DETACH)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Download Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(progress: Int, text: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading Video")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .build()


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1

        const val ACTION_START_DOWNLOAD = "ACTION_START_DOWNLOAD"
        const val ACTION_CANCEL_DOWNLOAD = "ACTION_CANCEL_DOWNLOAD"

        const val EXTRA_DOWNLOAD_ID = "EXTRA_DOWNLOAD_ID"
    }
}
