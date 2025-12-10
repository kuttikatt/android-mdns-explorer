package com.example.androidmdnsexplorer.presentation.auth


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidmdnsexplorer.R
import com.example.androidmdnsexplorer.data.repo.AuthRepository
import com.example.androidmdnsexplorer.databinding.ActivityLoginBinding
import com.example.androidmdnsexplorer.presentation.home.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val vm: AuthViewModel by viewModels()

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("LOGIN_DEBUG", "ActivityResult code = ${result.resultCode}")

            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d("LOGIN_DEBUG", "Google account = ${account.email}")
                    Log.d("LOGIN_DEBUG", "ID Token = ${account.idToken}")

                    val idToken = account.idToken
                    if (idToken != null) {
                        vm.signInWithGoogleIdToken(idToken)
                    } else {
                        Log.e("LOGIN_DEBUG", "ID token is null")
                        binding.status.text = "Google Sign-In failed: Token null"
                    }

                } catch (e: Exception) {
                    Log.e("LOGIN_DEBUG", "Google Sign-In failed", e)
                    binding.status.text = "Google Sign-In failed: ${e.message}"
                }
            } else {
                Log.e(
                    "LOGIN_DEBUG",
                    "Google Sign-In cancelled or failed, code: ${result.resultCode}"
                )
                binding.status.text = "Cancelled or failed."
            }
            binding.progressBar.visibility = View.GONE
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (vm.isSignedIn()) vm.refreshSilently()


        binding.googleSignIn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val client =
                AuthRepository(this)
                    .googleClient(getString(R.string.default_web_client_id))
            launcher.launch(client.signInIntent)
        }


        lifecycleScope.launchWhenStarted {
            vm.state.collect { s ->
                binding.status.text = s.error ?: ""
                if (s.signedIn && !s.loading) goHome()
            }
        }
    }


    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}