package com.rosaliscagroup.admin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rosaliscagroup.admin.data.entity.Activity
import com.rosaliscagroup.admin.data.entity.Project
import com.rosaliscagroup.admin.data.entity.Location
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor() : HomeRepository {
    private val db: FirebaseFirestore = Firebase.firestore

    override suspend fun getActivitiesCount(): Int {
        val snapshot = db.collection("activities").get().await()
        return snapshot.size()
    }

    override suspend fun getEquipmentsCount(): Int {
        val snapshot = db.collection("equipments").get().await()
        return snapshot.size()
    }

    override suspend fun getLocationsCount(): Int {
        val snapshot = db.collection("locations").get().await()
        return snapshot.size()
    }

    override suspend fun getProjectsCount(): Int {
        val snapshot = db.collection("projects").get().await()
        return snapshot.size()
    }

    override suspend fun getUsersCount(): Int {
        val snapshot = db.collection("users").get().await()
        return snapshot.size()
    }

    override suspend fun getRecentActivities(limit: Int): List<Activity> {
        val snapshot = db.collection("activities")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id
            val createdAtTimestamp = doc.getTimestamp("createdAt")
            val createdAt = createdAtTimestamp?.toDate()?.time ?: 0L
            val details = doc.getString("details") ?: ""
            val equipmentId = doc.getString("equipmentId") ?: ""
            val locationId = doc.getString("locationId") ?: ""
            val projectId = doc.getString("projectId") ?: ""
            val type = doc.getString("type") ?: ""
            Activity(
                id = id,
                createdAt = createdAt,
                details = details,
                equipmentId = equipmentId,
                locationId = locationId,
                projectId = projectId,
                type = type
            )
        }
    }

    override suspend fun getKondisiStat(): Map<String, Int> {
        val snapshot = db.collection("equipments").get().await()
        return snapshot.documents
            .mapNotNull { it.getString("kondisi") }
            .groupingBy { it }.eachCount()
    }

    override suspend fun getProjects(): List<Project> {
        val snapshot = db.collection("projects").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id
            val name = doc.getString("name") ?: return@mapNotNull null
            val address = doc.getString("address") ?: ""
            Project(id = id, name = name, address = address)
        }
    }

    override suspend fun getLocations(): List<Location> {
        val snapshot = db.collection("locations").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id
            val name = doc.getString("name") ?: return@mapNotNull null
            val address = doc.getString("address") ?: ""
            Location(id = id, name = name, address = address)
        }
    }
}
