package com.daylantern.suratatmaluhur.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.databinding.CardNotifikasiBinding
import com.daylantern.suratatmaluhur.entities.Notifikasi

class RvKotakPesanAdapter(private val list: List<Notifikasi>):
    RecyclerView.Adapter<RvKotakPesanAdapter.ViewHolder>() {
    
    private lateinit var context: Context
    private var listener: ((Notifikasi) -> Unit)? = null
    private var listenerDelete: ((Int) -> Unit)? = null
    
    fun setOnClickListener(action: (Notifikasi) -> Unit) {
        this.listener = action
    }
    
    fun setOnDeleteListener(action: (Int) -> Unit) {
        this.listenerDelete = action
    }
    
    inner class ViewHolder(val binding: CardNotifikasiBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(notifikasi: Notifikasi){
            binding.apply {
                btnHapusPesan.setOnClickListener {
                    listenerDelete?.invoke(notifikasi.idNotifikasi)
                }
                itemView.setOnClickListener { listener?.invoke(notifikasi) }
                
                if(notifikasi.statusDibaca == "Belum")
                    itemView.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.grey_200))
                else
                    itemView.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.white))
                tvJudulPesan.text = notifikasi.judul
                val noSurat = if(notifikasi.noSurat.isNullOrEmpty()) "---" else notifikasi.noSurat
                tvDeskripsiPesan.text = "[$noSurat] : ${notifikasi.deskripsi}"
                tvTglPesan.text = Constants.showDate(notifikasi.tglNotifikasi, false, isEnterDate = false)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = CardNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun getItemCount(): Int = list.size
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
    
}