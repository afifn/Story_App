package com.afifny.storysub.ui.main.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afifny.storysub.R
import com.afifny.storysub.data.remote.response.ListStoryItem
import com.afifny.storysub.databinding.ActivityStoryDetailBinding
import com.afifny.storysub.utils.withLocalDateFormat
import com.bumptech.glide.Glide

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    companion object {
        const val EXTRA_DATA: String = "data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        val storyItem = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA) as ListStoryItem
        setupDetail(storyItem)
    }

    private fun setupDetail(storyItem: ListStoryItem) {
        supportActionBar?.title = getString(R.string.story)
        binding.tvName.text = storyItem.name
        binding.tvDescription.text = storyItem.description
        binding.tvDate.withLocalDateFormat(storyItem.createdAt.toString())
        Glide.with(this)
            .load(storyItem.photoUrl)
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .into(binding.imgStory)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}