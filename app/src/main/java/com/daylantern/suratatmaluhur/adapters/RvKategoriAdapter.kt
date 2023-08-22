package com.daylantern.suratatmaluhur.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.suratatmaluhur.databinding.CardKategoriBagianBinding
import com.daylantern.suratatmaluhur.entities.Kategori

class RvKategoriAdapter(private val kategoriList: List<Kategori>): RecyclerView.Adapter<RvKategoriAdapter.RvKategoriAdapterViewHolder>() {
    
    private var listener: ((Kategori) -> Unit)? = null
    private var deleteListener: ((String) -> Unit)? = null
    private var ubahListener: ((Kategori) -> Unit)? = null
    
    fun setOnClickListener(action: (Kategori) -> Unit){
        this.listener = action
    }
    
    fun setOnUbahListener(action: (Kategori) -> Unit){
        this.ubahListener = action
    }
    
    fun setOnDeleteListener(action: (String) -> Unit){
        this.deleteListener = action
    }
    
    inner class RvKategoriAdapterViewHolder(private var binding: CardKategoriBagianBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(kategori: Kategori){
            binding.apply {
                tvKode.text = kategori.kodeKategori
                tvDeskripsi.text = kategori.deskripsi
                btnHapus.setOnClickListener { deleteListener?.invoke(kategori.kodeKategori) }
                btnUbah.setOnClickListener { ubahListener?.invoke(kategori) }
            }
            itemView.setOnClickListener {listener?.invoke(kategori) }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvKategoriAdapterViewHolder {
        val binding = CardKategoriBagianBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RvKategoriAdapterViewHolder(binding)
    }
    
    override fun getItemCount(): Int = kategoriList.size
    
    override fun onBindViewHolder(holder: RvKategoriAdapterViewHolder, position: Int) {
        holder.bind(kategoriList[position])
    }
    
}