package com.daylantern.arsipsuratpembinaan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daylantern.arsipsuratpembinaan.databinding.CardPreviewFileBinding

class RvFileSuratAdapter(private val listFile: List<String>): RecyclerView.Adapter<RvFileSuratAdapter.RvFileSuratViewHolder>() {

    private var listener: OnItemClickListener? = null

    fun setOnItemClicked(listener: OnItemClickListener){
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onImageClicked(linkFile: String)
    }

    inner class RvFileSuratViewHolder(val binding: CardPreviewFileBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(linkFile: String){
            binding.apply {
                imgHapusFile.visibility = View.GONE
                Glide.with(itemView).load(linkFile).into(imgFileSurat)
                itemView.setOnClickListener {
                    listener?.onImageClicked(linkFile)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvFileSuratViewHolder {
        return RvFileSuratViewHolder(CardPreviewFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RvFileSuratViewHolder, position: Int) {
        holder.bind(listFile[position])
    }

    override fun getItemCount(): Int {
        return listFile.size
    }
}