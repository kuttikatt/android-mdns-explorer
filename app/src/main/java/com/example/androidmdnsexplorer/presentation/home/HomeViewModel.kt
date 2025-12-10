package com.example.androidmdnsexplorer.presentation.home


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidmdnsexplorer.data.db.DeviceEntity
import com.example.androidmdnsexplorer.data.discovery.NsdDiscoveryManager
import com.example.androidmdnsexplorer.data.repo.DeviceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class HomeViewModel(context: Context) : ViewModel() {
    private val repo = DeviceRepository(context)
    private val nsd = NsdDiscoveryManager(context)


    val devices: StateFlow<List<DeviceEntity>> =
        repo.observeDevices().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    fun startDiscovery() {
        nsd.start()
        viewModelScope.launch {
            nsd.events.collect { d -> repo.upsertDiscovery(d) }
        }
    }


    fun stopDiscovery() {
        nsd.stop()
    }
}