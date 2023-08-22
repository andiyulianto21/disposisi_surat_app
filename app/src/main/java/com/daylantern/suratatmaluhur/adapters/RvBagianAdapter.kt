package com.daylantern.suratatmaluhur.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.suratatmaluhur.databinding.CardKategoriBagianBinding
import com.daylantern.suratatmaluhur.entities.Bagian

class RvBagianAdapter(private val bagianList: List<Bagian>): RecyclerView.Adapter<RvBagianAdapter.RvBagianAdapterViewHolder>() {
    
    private var listener: ((Bagian) -> Unit)? = null
    private var deleteListener: ((String) -> Unit)? = null
    private var ubahListener: ((Bagian) -> Unit)? = null
    
    fun setOnClickListener(action: (Bagian) -> Unit){
        this.listener = action
    }
    
    fun setOnUbahListener(action: (Bagian) -> Unit){
        this.ubahListener = action
    }
    
    fun setOnDeleteListener(action: (String) -> Unit){
        this.deleteListener = action
    }
    
    inner class RvBagianAdapterViewHolder(private var binding: CardKategoriBagianBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bagian: Bagian){
            binding.apply {
                tvKode.text = bagian.kodeBagian
                tvDeskripsi.text = bagian.deskripsi
                btnHapus.setOnClickListener { deleteListener?.invoke(bagian.kodeBagian) }
                btnUbah.setOnClickListener { ubahListener?.invoke(bagian) }
            }
            itemView.setOnClickListener {listener?.invoke(bagian) }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvBagianAdapterViewHolder {
        val binding = CardKategoriBagianBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RvBagianAdapterViewHolder(binding)
    }
    
    override fun getItemCount(): Int = bagianList.size
    
    override fun onBindViewHolder(holder: RvBagianAdapterViewHolder, position: Int) {
        holder.bind(bagianList[position])
    }
    
}