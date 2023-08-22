package com.daylantern.suratatmaluhur.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.suratatmaluhur.databinding.CardInstansiBinding
import com.daylantern.suratatmaluhur.entities.Instansi

class RvInstansiAdapter(private val instansiList: List<Instansi>): RecyclerView.Adapter<RvInstansiAdapter.RvInstansiAdapterViewHolder>() {
    
    private var listener: ((Instansi) -> Unit)? = null
    private var deleteListener: ((Int) -> Unit)? = null
    private var ubahListener: ((Instansi) -> Unit)? = null
    
    fun setOnClickListener(action: (Instansi) -> Unit){
        this.listener = action
    }
    
    fun setOnUbahListener(action: (Instansi) -> Unit){
        this.ubahListener = action
    }
    
    fun setOnDeleteListener(action: (Int) -> Unit){
        this.deleteListener = action
    }
    
    inner class RvInstansiAdapterViewHolder(private var binding: CardInstansiBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(instansi: Instansi){
            binding.apply {
                tvNamaInstansi.text = instansi.namaInstansi
                btnHapus.setOnClickListener { deleteListener?.invoke(instansi.idInstansi) }
                btnUbah.setOnClickListener { ubahListener?.invoke(instansi) }
            }
            itemView.setOnClickListener {listener?.invoke(instansi) }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvInstansiAdapterViewHolder {
        val binding = CardInstansiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RvInstansiAdapterViewHolder(binding)
    }
    
    override fun getItemCount(): Int = instansiList.size
    
    override fun onBindViewHolder(holder: RvInstansiAdapterViewHolder, position: Int) {
        holder.bind(instansiList[position])
    }
    
}