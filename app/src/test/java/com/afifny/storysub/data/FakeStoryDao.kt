package com.afifny.storysub.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afifny.storysub.data.local.room.StoryDao
import com.afifny.storysub.data.remote.response.ListStoryItem

class FakeStoryDao : StoryDao {
    private var story = mutableListOf<ListStoryItem>()

    override suspend fun insertStory(story: List<ListStoryItem>) {

    }

    override fun getAllStory(): PagingSource<Int, ListStoryItem> {
        return FakePagingSource(story)
    }

    override suspend fun deleteAll() {
        story.clear()
    }

    class FakePagingSource(private val data: MutableList<ListStoryItem>) :
        PagingSource<Int, ListStoryItem>() {
        @Suppress("SameReturnValue")
        override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int = 0

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
            return LoadResult.Page(data, 0, 1)
        }

    }
}