package com.daylantern.suratatmaluhur.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.databinding.CardPreviewFileBinding
import com.daylantern.suratatmaluhur.models.FileSuratModel

class RvPreviewFileAdapter(private val items: MutableList<FileSuratModel>):
    RecyclerView.Adapter<RvPreviewFileAdapter.RvPreviewFileViewHolder>() {

    interface Listener {
        fun onItemClicked(image: Bitmap)
    }

    private var listener: Listener? = null
    private lateinit var context: Context

    fun setOnItemClicked(listener: Listener){
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(item: FileSuratModel) {
        items.add(item)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyDataSetChanged()
    }
    
    fun size(): Int{
        return items.size
    }

    inner class RvPreviewFileViewHolder(val binding: CardPreviewFileBinding): ViewHolder(binding.root){
        fun bind(position: Int){
            val item = items[position]

            binding.apply {
                if(item.bitmap == null){
                    imgFileSurat.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.outline_picture_as_pdf_24))
                }else{
                    imgFileSurat.setImageBitmap(item.bitmap)
                    itemView.setOnClickListener { item.bitmap.let { bitmap -> listener?.onItemClicked(bitmap) } }
                }
                imgHapusFile.setOnClickListener {
                    removeItem(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvPreviewFileViewHolder {
        context = parent.context
        val view = CardPreviewFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RvPreviewFileViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvPreviewFileViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}