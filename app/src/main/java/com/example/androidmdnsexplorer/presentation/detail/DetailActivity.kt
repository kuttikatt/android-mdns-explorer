package com.example.androidmdnsexplorer.presentation.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidmdnsexplorer.databinding.ActivityDetailBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val vm = DetailViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val types =
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
            val i = insets.getInsets(types)
            v.setPadding(i.left, i.top, i.right, i.bottom)
            insets
        }


        val name = intent.getStringExtra("name") ?: "Device"
        val ip = intent.getStringExtra("ip")
        binding.tvDeviceHeader.text = "$name (IP: ${ip ?: "—"})"


        lifecycleScope.launch {
            vm.state.collectLatest { st ->
                when (st) {
                    is DetailState.Data -> {
                        binding.tvPublicIp.text = "\nPublic IP: ${st.ip}"
                        binding.tvGeo.text =
                            "\nGeo: ${st.city ?: "?"}, ${st.region ?: "?"}, ${st.country ?: "?"}"
                        binding.tvOrg.text = "\nOrg/Carrier: ${st.org ?: "?"}"
                    }

                    is DetailState.Error -> {
                        binding.tvPublicIp.text = st.msg
                    }

                    else -> {}
                }
            }
        }


        vm.load()
    }
}