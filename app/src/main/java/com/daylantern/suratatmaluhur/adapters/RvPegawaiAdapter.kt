package com.daylantern.suratatmaluhur.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.suratatmaluhur.databinding.CardPegawaiBinding
import com.daylantern.suratatmaluhur.entities.Pegawai

class RvPegawaiAdapter(private val pegawaiList: List<Pegawai>): RecyclerView.Adapter<RvPegawaiAdapter.ViewHolder>() {
    
    private var listener: ((Pegawai) -> Unit)? = null
    private var ubahListener: ((Pegawai) -> Unit)? = null
    private var deleteListener: ((Int) -> Unit)? = null
    
    fun setOnClickListener(action: (Pegawai) -> Unit){
        this.listener = action
    }
    
    fun setOnUbahListener(action: (Pegawai) -> Unit){
        this.ubahListener = action
    }
    
    fun setOnDeleteListener(action: (Int) -> Unit) {
        this.deleteListener = action
    }
    
    inner class ViewHolder(private var binding: CardPegawaiBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(pegawai: Pegawai){
            binding.apply {
                tvNamaPegawai.text = pegawai.nama
                tvEmail.text = pegawai.email
                tvJabatan.text = pegawai.jabatan
                tvLevelAkses.text = pegawai.levelAkses
                btnUbah.setOnClickListener {
                    ubahListener?.invoke(pegawai)
                }
                btnHapus.setOnClickListener {
                    deleteListener?.invoke(pegawai.idPegawai)
                }
            }
            itemView.setOnClickListener {
                listener?.invoke(pegawai)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPegawaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pegawaiList[position])
    }
    
    
    override fun getItemCount(): Int = pegawaiList.size
    
    
}