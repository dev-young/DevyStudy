package io.ymsoft.devystudy.contentsprovider.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.ymsoft.devystudy.contentsprovider.models.Bucket
import io.ymsoft.devystudy.databinding.ItemBucketBinding

class BucketListAdapter(
    private var clickListener: ((Int, Bucket) -> Unit)? = null,
    private var longClickListener: ((Int, Bucket) -> Boolean)? = null
) : ListAdapter<Bucket, BucketListAdapter.BucketViewHolder>(object :
    DiffUtil.ItemCallback<Bucket>() {
    override fun areItemsTheSame(oldItem: Bucket, newItem: Bucket): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Bucket, newItem: Bucket): Boolean {
        return oldItem.numberOfImg == newItem.numberOfImg
    }

}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBucketBinding.inflate(layoutInflater, parent, false)
        val vh = BucketViewHolder(binding)
        binding.root.setOnClickListener {
            clickListener?.invoke(vh.adapterPosition, getItem(vh.adapterPosition))
        }
        binding.root.setOnLongClickListener {
            longClickListener?.invoke(vh.adapterPosition, getItem(vh.adapterPosition)) ?: false
        }
        return vh
    }

    override fun onBindViewHolder(holder: BucketViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class BucketViewHolder(
        val binding: ItemBucketBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(model: Bucket) {
            binding.bucket = model
            Glide.with(binding.root.context)
                .load(model.firstPic)
                .centerCrop()
                .into(binding.imgView)
        }

    }

}