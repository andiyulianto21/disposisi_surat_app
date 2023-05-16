package com.daylantern.arsipsuratpembinaan.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.arsipsuratpembinaan.databinding.CardInstansiBinding
import com.daylantern.arsipsuratpembinaan.entities.Instansi

class RvInstansiAdapter(private val instansiList: List<Instansi>): RecyclerView.Adapter<RvInstansiAdapter.RvInstansiAdapterViewHolder>() {
    
    private var listener: ((Instansi) -> Unit)? = null
    
    fun setOnClickListener(action: (Instansi) -> Unit){
        this.listener = action
    }
    
    inner class RvInstansiAdapterViewHolder(private var binding: CardInstansiBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.tvNamaInstansi.text = instansiList[position].namaInstansi
            itemView.setOnClickListener {
                listener?.invoke(instansiList[position])
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvInstansiAdapterViewHolder {
        val binding = CardInstansiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RvInstansiAdapterViewHolder(binding)
    }
    
    override fun getItemCount(): Int = instansiList.size
    
    override fun onBindViewHolder(holder: RvInstansiAdapterViewHolder, position: Int) {
        holder.bind(position)
    }
    
}