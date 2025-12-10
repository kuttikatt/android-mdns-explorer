package com.yourcompany.androidassignment.utils


import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.core.content.edit


class EncryptedPrefsManager(context: Context) {
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val prefs = EncryptedSharedPreferences.create(
        "secure_prefs", masterKey, context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )


    fun putToken(token: String?) {
        prefs.edit { putString("id_token", token) }
    }


    fun getToken(): String? = prefs.getString("id_token", null)


    fun clear() {
        prefs.edit { clear() }
    }
}