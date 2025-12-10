package com.example.androidmdnsexplorer.data.discovery


import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow


/**
 * Discovers common mDNS service types (feel free to add more if available at home).
 */
class NsdDiscoveryManager(private val context: Context) {
    private val nsd: NsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager


    // Emit basic discovered info
    private val _events = MutableSharedFlow<DiscoveredDevice>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<DiscoveredDevice> = _events


    private val types = listOf(
        "_airplay._tcp.",
        "_googlecast._tcp.",
        "_http._tcp."
    )


    private val listeners = mutableListOf<NsdManager.DiscoveryListener>()


    fun start() {
        stop()
        types.forEach { type ->
            val listener = object : NsdManager.DiscoveryListener {
                override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {}
                override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {}
                override fun onDiscoveryStarted(serviceType: String?) {}
                override fun onDiscoveryStopped(serviceType: String?) {}
                override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                    _events.tryEmit(DiscoveredDevice(serviceInfo.serviceName, null, false))
                }

                override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                    Log.d("NSD", "Found: ${serviceInfo.serviceName} @ ${serviceInfo.serviceType}")

                    nsd.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(
                            serviceInfo: NsdServiceInfo?,
                            errorCode: Int
                        ) {
                            Log.w("NSD", "Resolve failed: $errorCode for $serviceInfo")

                        }

                        override fun onServiceResolved(resolved: NsdServiceInfo) {
                            Log.d("NSD", "Resolved: ${resolved.serviceName} -> ${resolved.host?.hostAddress}:${resolved.port}")

                            val host = resolved.host?.hostAddress
                            _events.tryEmit(DiscoveredDevice(resolved.serviceName, host, true))
                        }
                    })
                }
            }
            nsd.discoverServices(type, NsdManager.PROTOCOL_DNS_SD, listener)
            listeners += listener
        }
    }


    fun stop() {
        listeners.forEach { runCatching { nsd.stopServiceDiscovery(it) } }
        listeners.clear()
    }
}


data class DiscoveredDevice(val name: String, val ip: String?, val online: Boolean)