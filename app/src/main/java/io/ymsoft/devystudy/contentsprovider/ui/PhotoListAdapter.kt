package io.ymsoft.devystudy.contentsprovider.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.ymsoft.devystudy.contentsprovider.models.Photo
import io.ymsoft.devystudy.databinding.ItemPhotoBinding

class PhotoListAdapter(
    private var clickListener: ((Int, Photo) -> Unit)? = null,
    private var longClickListener: ((Int, Photo) -> Boolean)? = null
) : ListAdapter<Photo, PhotoListAdapter.PhotoViewHolder>(object :
    DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.size == newItem.size
    }

}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(layoutInflater, parent, false)
        val vh = PhotoViewHolder(binding)
        binding.root.setOnClickListener {
            clickListener?.invoke(vh.adapterPosition, getItem(vh.adapterPosition))
        }
        binding.root.setOnLongClickListener {
            longClickListener?.invoke(vh.adapterPosition, getItem(vh.adapterPosition)) ?: false
        }
        return vh
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val Photo = getItem(position)
        holder.onBind(Photo)
    }

    inner class PhotoViewHolder(
        val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(model: Photo) {
            binding.photo = model
            Glide.with(binding.root.context)
                .load(model.uri)
                .centerCrop()
                .into(binding.imgView)
        }

    }

}