package com.afifny.storysub.data

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.afifny.storysub.data.local.room.RemoteKeysDao
import com.afifny.storysub.data.local.room.StoryDao
import com.afifny.storysub.data.local.room.StoryDatabase
import org.mockito.Mockito

class FakeStoryDatabase : StoryDatabase() {
    override fun storyDao(): StoryDao {
        return FakeStoryDao()
    }

    override fun remoteKeysDao(): RemoteKeysDao {
        return FakeRemoteKeysDao()
    }

    override fun clearAllTables() {

    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return Mockito.mock(InvalidationTracker::class.java)
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        return Mockito.mock(SupportSQLiteOpenHelper::class.java)
    }
}