package com.daylantern.suratatmaluhur.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.suratatmaluhur.databinding.RvLaporanSuratMasukBinding
import com.daylantern.suratatmaluhur.entities.SuratMasuk

class RvLaporanSuratMasukAdapter(val list: List<SuratMasuk>):
    RecyclerView.Adapter<RvLaporanSuratMasukAdapter.ViewHolder>() {
    
    inner class ViewHolder(private val binding: RvLaporanSuratMasukBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            binding.apply {
                tvNo.text = "${position+1}"
                tvNoSurat.text = list[position].noSurat
                tvInstansiPengirim.text = list[position].instansiPengirim
                tvPerihal.text = list[position].perihal
                tvTglSuratMasuk.text = list[position].tglSurat
                tvTglSuratDiterima.text = list[position].tglSuratDiinput
//                tvLampiran.text = list[position].lampiran
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvLaporanSuratMasukBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun getItemCount(): Int = list.size
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}