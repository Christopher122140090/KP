package com.rosaliscagroup.admin.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rosaliscagroup.admin.data.dao.ImageDao
import com.rosaliscagroup.admin.data.entity.Image

@Database(
    version = 1,
    entities = [Image::class],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}
