package com.rosaliscagroup.admin.di

import android.content.Context
import androidx.room.Room
import com.rosaliscagroup.admin.R
import com.rosaliscagroup.admin.data.AppDatabase
import com.rosaliscagroup.admin.data.RoomDbInitializer
import com.rosaliscagroup.admin.data.dao.ImageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        imageDaoProvider: Provider<ImageDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext, AppDatabase::class.java, context.getString(
                R.string.app_name
            )
        ).addCallback(
            /**
             * Attach [RoomDbInitializer] as callback to the database
             */
            RoomDbInitializer(context = context, imageDaoProvider = imageDaoProvider)
        )
            .build()
    }

    @Singleton
    @Provides
    fun provideMessageDao(appDatabase: AppDatabase): ImageDao = appDatabase.imageDao()
}
