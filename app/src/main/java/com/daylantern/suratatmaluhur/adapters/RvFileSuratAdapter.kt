package com.daylantern.suratatmaluhur.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.databinding.CardPreviewFileBinding

class RvFileSuratAdapter(private val listFile: List<String>) :
    RecyclerView.Adapter<RvFileSuratAdapter.RvFileSuratViewHolder>() {
    
    private var listener: OnItemClickListener? = null
    private lateinit var context: Context
    
    fun setOnItemClicked(listener: OnItemClickListener) {
        this.listener = listener
    }
    
    interface OnItemClickListener {
        fun onImageClicked(linkFile: String)
    }
    
    inner class RvFileSuratViewHolder(val binding: CardPreviewFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(linkFile: String) {
            val fileExtension = linkFile.substringAfterLast(".", "")
            binding.apply {
                imgHapusFile.visibility = View.GONE
                if (fileExtension != "pdf") Glide.with(itemView).load(linkFile).into(imgFileSurat)
                else Glide.with(itemView).load(AppCompatResources.getDrawable(
                    context, R.drawable.outline_picture_as_pdf_24)).into(imgFileSurat)
                itemView.setOnClickListener {
                    listener?.onImageClicked(linkFile)
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvFileSuratViewHolder {
        context = parent.context
        return RvFileSuratViewHolder(
            CardPreviewFileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    
    override fun onBindViewHolder(holder: RvFileSuratViewHolder, position: Int) {
        holder.bind(listFile[position])
    }
    
    override fun getItemCount(): Int {
        return listFile.size
    }
}