package com.example.androidmdnsexplorer.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class NetworkRepository {
    data class IpInfo(
        val ip: String,
        val city: String?,
        val region: String?,
        val country: String?,
        val org: String?,
        val loc: String?,
        val timezone: String?
    )

    private suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "android-mdns-explorer")
        }
        try {
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            stream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    @Throws(Exception::class)
    suspend fun getPublicIp(): String {
        val body = get("https://api.ipify.org?format=json")
        return JSONObject(body).getString("ip")
    }

    @Throws(Exception::class)
    suspend fun getIpInfo(ip: String): IpInfo {
        val body = get("https://ipinfo.io/$ip/geo")
        val json = JSONObject(body)
        return IpInfo(
            ip = json.optString("ip", ip),
            city = json.optString("city", null),
            region = json.optString("region", null),
            country = json.optString("country", null),
            org = json.optString("org", null),
            loc = json.optString("loc", null),
            timezone = json.optString("timezone", null)
        )
    }
}
