package com.afifny.storysub.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.afifny.storysub.data.AuthRepository
import com.afifny.storysub.data.StoryRepository
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.data.local.room.StoryDatabase
import com.afifny.storysub.data.remote.retrofit.ApiConfig

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        val pref = UserPref.getInstance(context.dataStore)
        return StoryRepository(database, apiService, pref)
    }

    fun providerAuthRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository(apiService)
    }
}