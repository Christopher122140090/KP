package com.rosaliscagroup.admin.data.entity

import com.google.firebase.Timestamp

data class Activity(
    val id: String = "",
    val createdAt: Timestamp? = null,
    val details: String = "",
    val equipmentId: String = "",
    val locationId: String = "",
    val projectId: String = "",
    val type: String = ""
)
