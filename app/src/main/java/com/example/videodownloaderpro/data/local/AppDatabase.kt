package com.example.videodownloaderpro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.videodownloaderpro.data.local.dao.DownloadDao
import com.example.videodownloaderpro.data.local.entity.DownloadItem

@Database(entities = [DownloadItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun downloadDao(): DownloadDao
}
