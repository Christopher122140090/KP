package com.rosaliscagroup.admin.data.entity

data class Activity(
    val id: String = "",
    val createdAt: Long = 0L,
    val details: String = "",
    val equipmentId: String = "",
    val locationId: String = "",
    val projectId: String = "",
    val type: String = ""
)
