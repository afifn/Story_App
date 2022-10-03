package com.afifny.storysub.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPref private constructor(private val dataStore: DataStore<Preferences>){
    companion object {
        @Volatile
        private var INSTANCE: UserPref?= null
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN = stringPreferencesKey("token")
        private val USER_ID = stringPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPref{
            return INSTANCE?: synchronized(this) {
                val instance = UserPref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getUser(): Flow<LoginResult> {
        return dataStore.data.map {
            pref ->
            LoginResult(
                pref[NAME_KEY] ?: "",
                pref[USER_ID] ?: "",
                pref[TOKEN] ?: ""
            )
        }
    }

    suspend fun saveUserLogin(user: LoginResult) {
        dataStore.edit {
            it[NAME_KEY] = user.name
            it[USER_ID] = user.userId
            it[TOKEN] = user.token
        }
    }

    suspend fun logout() {
        dataStore.edit {
            preference ->
            preference[TOKEN] = ""
        }
    }
}