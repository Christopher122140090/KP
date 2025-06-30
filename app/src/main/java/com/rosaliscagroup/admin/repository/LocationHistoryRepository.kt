package com.rosaliscagroup.admin.repository

import com.rosaliscagroup.admin.data.entity.LocationHistory
import kotlinx.coroutines.flow.Flow

interface LocationHistoryRepository {
    suspend fun getRecentLocationHistory(limit: Int = 3): List<LocationHistory>
    fun getRecentLocationHistoryRealtime(limit: Int = 3): Flow<List<LocationHistory>>
    suspend fun getAllLocationHistory(): List<LocationHistory>
}
