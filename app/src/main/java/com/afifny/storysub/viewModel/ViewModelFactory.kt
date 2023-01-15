package com.afifny.storysub.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.di.Injection
import com.afifny.storysub.ui.login.LoginViewModel
import com.afifny.storysub.ui.main.fragment.home.MainViewModel
import com.afifny.storysub.ui.main.fragment.maps.MapsViewModel
import com.afifny.storysub.ui.main.fragment.story.StoryViewModel
import com.afifny.storysub.ui.register.RegisterViewModel

class ViewModelFactory(private val pref: UserPref) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(DataStoreViewModel::class.java) -> {
                DataStoreViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(Injection.providerAuthRepository()) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.providerAuthRepository()) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(Injection.provideRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class" + modelClass.name)
        }

    }
}