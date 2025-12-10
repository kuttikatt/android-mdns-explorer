package com.example.androidmdnsexplorer.presentation.home


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmdnsexplorer.databinding.ActivityHomeBinding
import com.example.androidmdnsexplorer.presentation.detail.DetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var vm: HomeViewModel
    private lateinit var adapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        vm = HomeViewModel(this)


        adapter = DeviceAdapter { d ->
            startActivity(Intent(this, DetailActivity::class.java).apply {
                putExtra("name", d.displayName)
                putExtra("ip", d.ip)
            })
        }
        binding.rvDevices.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        binding.rvDevices.adapter = adapter


        lifecycleScope.launch {
            vm.devices.collectLatest { adapter.submit(it) }
        }
    }


    override fun onStart() {
        super.onStart(); vm.startDiscovery()
    }

    override fun onStop() {
        vm.stopDiscovery(); super.onStop()
    }
}