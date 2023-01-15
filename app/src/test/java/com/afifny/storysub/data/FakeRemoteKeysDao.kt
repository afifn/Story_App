package com.afifny.storysub.data

import com.afifny.storysub.data.local.entity.RemoteKeys
import com.afifny.storysub.data.local.room.RemoteKeysDao

class FakeRemoteKeysDao : RemoteKeysDao {
    private val remoteKeys = mutableListOf<RemoteKeys>()

    override suspend fun insertAll(remoteKeys: List<RemoteKeys>) {

    }

    override suspend fun getRemoteKeysId(id: String): RemoteKeys? {
        return remoteKeys.firstOrNull() {
            it.id == id
        }
    }

    override suspend fun deleteRemoteKeys() {
        remoteKeys.clear()
    }
}