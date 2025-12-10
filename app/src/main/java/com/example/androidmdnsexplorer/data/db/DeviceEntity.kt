package com.example.androidmdnsexplorer.data.db


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val serviceName: String,
    val ip: String?,
    val displayName: String,
    val lastSeen: Long,
    val isOnline: Boolean
)