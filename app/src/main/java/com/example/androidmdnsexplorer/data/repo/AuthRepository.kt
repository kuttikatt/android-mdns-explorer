package com.example.androidmdnsexplorer.data.repo

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yourcompany.androidassignment.utils.EncryptedPrefsManager
import kotlinx.coroutines.tasks.await


class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()


    fun googleClient(webClientId: String): GoogleSignInClient =
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
        )


    suspend fun handleGoogleIdToken(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
        val fresh = auth.currentUser?.getIdToken(true)?.await()?.token
        if (fresh != null) EncryptedPrefsManager(context).putToken(fresh)
    }


    fun isLoggedIn(): Boolean = auth.currentUser != null


    suspend fun refreshTokenOrLogout(): String? = try {
        val token = auth.currentUser?.getIdToken(true)?.await()?.token
        if (token != null) EncryptedPrefsManager(context).putToken(token)
        token
    } catch (e: Exception) {
        logout()
        null
    }


    fun logout() {
        EncryptedPrefsManager(context).clear()
        auth.signOut()
    }
}