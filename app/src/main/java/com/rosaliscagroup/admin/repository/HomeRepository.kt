package com.rosaliscagroup.admin.repository

import com.rosaliscagroup.admin.data.entity.Activity
import javax.inject.Inject
import javax.inject.Singleton

interface HomeRepository {
    suspend fun getActivitiesCount(): Int
    suspend fun getEquipmentsCount(): Int
    suspend fun getLocationsCount(): Int
    suspend fun getProjectsCount(): Int
    suspend fun getUsersCount(): Int
    suspend fun getRecentActivities(limit: Int): List<Activity>
    suspend fun getKondisiStat(): Map<String, Int>
}
