package com.example.androidmdnsexplorer.data.repo


import android.content.Context
import com.example.androidmdnsexplorer.data.db.AppDatabase
import com.example.androidmdnsexplorer.data.db.DeviceEntity
import com.example.androidmdnsexplorer.data.discovery.DiscoveredDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


class DeviceRepository(context: Context) {
    private val dao = AppDatabase.get(context).deviceDao()


    fun observeDevices(): Flow<List<DeviceEntity>> = dao.observeAll()


    suspend fun upsertDiscovery(d: DiscoveredDevice) {
        val now = System.currentTimeMillis()
        val current = dao.observeAll().first().find { it.serviceName == d.name }
        if (current == null) {
            dao.upsert(DeviceEntity(d.name, d.ip, d.name, now, d.online))
        } else {
            dao.updateStatus(d.name, d.online, now, d.ip)
        }
    }
}