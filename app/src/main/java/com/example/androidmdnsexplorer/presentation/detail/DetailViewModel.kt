package com.example.androidmdnsexplorer.presentation.detail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidmdnsexplorer.data.network.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val net = NetworkRepository()

    private val _state = MutableStateFlow<DetailState>(DetailState.Idle)
    val state: StateFlow<DetailState> = _state

    fun load() {
        viewModelScope.launch {
            try {
                _state.value = DetailState.Loading
                val ip = net.getPublicIp()           // suspend → runs on IO
                val info = net.getIpInfo(ip)         // suspend → runs on IO
                _state.value = DetailState.Data(ip, info.city, info.region, info.country, info.org)
            } catch (e: Exception) {
                _state.value = DetailState.Error(e.message ?: "Network error")
            }
        }
    }
}

sealed class DetailState {
    object Idle : DetailState()
    object Loading : DetailState()
    data class Data(
        val ip: String,
        val city: String?, val region: String?, val country: String?, val org: String?
    ) : DetailState()

    data class Error(val msg: String) : DetailState()
}