package com.example.androidmdnsexplorer.presentation.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmdnsexplorer.data.db.DeviceEntity
import com.example.androidmdnsexplorer.databinding.ItemDeviceBinding


class DeviceAdapter(private val onClick: (DeviceEntity) -> Unit) :
    RecyclerView.Adapter<DeviceAdapter.VH>() {
    private val items = mutableListOf<DeviceEntity>()


    fun submit(list: List<DeviceEntity>) {
        items.clear(); items.addAll(list); notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }


    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])


    inner class VH(private val b: ItemDeviceBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(d: DeviceEntity) {
            b.tvName.text = d.displayName
            b.tvIp.text = d.ip ?: "—"
            b.tvStatus.text = if (d.isOnline) "Online" else "Offline"
            b.root.setOnClickListener { onClick(d) }
        }
    }
}