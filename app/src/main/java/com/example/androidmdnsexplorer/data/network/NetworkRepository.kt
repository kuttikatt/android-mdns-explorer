package com.example.androidmdnsexplorer.data.network


import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
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


    @Throws(Exception::class)
    fun getPublicIp(): String {
        val url = URL("https://api.ipify.org?format=json")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
        }
        conn.inputStream.use { stream ->
            val resp = BufferedReader(InputStreamReader(stream)).readText()
            val json = JSONObject(resp)
            return json.getString("ip")
        }
    }


    @Throws(Exception::class)
    fun getIpInfo(ip: String): IpInfo {
        val url = URL("https://ipinfo.io/$ip/geo")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
        }
        conn.inputStream.use { stream ->
            val resp = BufferedReader(InputStreamReader(stream)).readText()
            val json = JSONObject(resp)
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
}