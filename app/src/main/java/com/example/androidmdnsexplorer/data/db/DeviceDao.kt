package com.example.androidmdnsexplorer.data.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY displayName")
    fun observeAll(): Flow<List<DeviceEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(device: DeviceEntity)


    @Query("UPDATE devices SET isOnline = :online, lastSeen = :ts, ip = :ip WHERE serviceName = :name")
    suspend fun updateStatus(name: String, online: Boolean, ts: Long, ip: String?)
}