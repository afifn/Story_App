package com.afifny.storysub.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.afifny.storysub.R
import com.afifny.storysub.databinding.ItemListBinding
import com.afifny.storysub.model.ListStoryItem
import com.afifny.storysub.ui.main.fragment.home.HomeFragment
import com.bumptech.glide.Glide

class StoryAdapter: RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    private val listStoryItem = ArrayList<ListStoryItem>()
    private var onClickItem: OnClickItem? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        onClickItem?.let { viewHolder.bind(listStoryItem[position], it) }
    }

    override fun getItemCount(): Int {
        return listStoryItem.size
    }


    fun setAdapter(list: List<ListStoryItem>?) {
        if (list != null) {
            listStoryItem.clear()
            listStoryItem.addAll(list)
        }
    }

    inner class ViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem, onClickItem: OnClickItem) {
            binding.apply {
                tvName.text = story.name
                tvDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imgStory)
            }
            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.imgStory, "image"),
                        Pair(binding.tvName, "name"),
                        Pair(binding.tvDescription, "description")
                    )
                val position: Int = adapterPosition
                onClickItem.onClickItem(story, position, optionsCompat)
            }
        }
    }
    interface OnClickItem{
        fun onClickItem(story: ListStoryItem, position: Int, optionsCompat: ActivityOptionsCompat)
    }
    fun setOnClick(onClickItem: HomeFragment) {
        this.onClickItem = onClickItem
    }
}
