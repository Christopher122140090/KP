package com.rosaliscagroup.admin.data.entity

data class Location(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val contactPerson: String = "",
    val createdAt: Long = 0L,
    val createdBy: String = "",
    val description: String = "",
    val status: String = "",
    val type: String = ""
)
