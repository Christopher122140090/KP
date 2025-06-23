package com.rosaliscagroup.admin.repository

import com.rosaliscagroup.admin.data.entity.Activity
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.data.entity.Project
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getActivitiesCount(): Int
    suspend fun getEquipmentsCount(): Int
    suspend fun getLocationsCount(): Int
    suspend fun getProjectsCount(): Int
    suspend fun getUsersCount(): Int
    suspend fun getRecentActivities(limit: Int): List<Activity>
    fun getRecentActivitiesRealtime(limit: Int): Flow<List<Activity>>
    suspend fun getKondisiStat(): Map<String, Int>
    suspend fun getProjects(): List<Project>
    suspend fun getLocations(): List<Location>
    suspend fun getNewEquipmentsThisWeek(): Int
}
