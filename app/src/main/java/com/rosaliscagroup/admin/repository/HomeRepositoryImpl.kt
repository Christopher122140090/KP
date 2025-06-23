package com.rosaliscagroup.admin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rosaliscagroup.admin.data.entity.Activity
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
        val snapshot = db.collection("activities").orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
        return snapshot.documents.mapNotNull { it.toObject(Activity::class.java) }
    }

    override suspend fun getKondisiStat(): Map<String, Int> {
        val snapshot = db.collection("equipments").get().await()
        return snapshot.documents
            .mapNotNull { it.getString("kondisi") }
            .groupingBy { it }.eachCount()
    }
}
