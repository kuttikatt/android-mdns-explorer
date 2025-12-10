package com.example.androidmdnsexplorer.presentation.auth


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidmdnsexplorer.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(app)


    data class UiState(
        val loading: Boolean = false,
        val signedIn: Boolean = false,
        val error: String? = null
    )


    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state


    fun isSignedIn() = repo.isLoggedIn()


    fun refreshSilently() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            val token = repo.refreshTokenOrLogout()
            _state.value = UiState(loading = false, signedIn = token != null)
        }
    }


    fun signInWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            try {
                Log.d("AUTH_DEBUG", "Signing in with Google token...")

                _state.value = _state.value.copy(loading = true)
                repo.handleGoogleIdToken(idToken)
                _state.value = UiState(loading = false, signedIn = true)
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Firebase Auth failed", e)

                _state.value = UiState(loading = false, signedIn = false, error = e.message)
            }
        }
    }


    fun logout() {
        repo.logout()
    }
}