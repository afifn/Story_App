package com.afifny.storysub.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.databinding.ActivitySplashBinding
import com.afifny.storysub.ui.login.LoginActivity
import com.afifny.storysub.ui.main.MainActivity
import com.afifny.storysub.viewModel.DataStoreViewModel
import com.afifny.storysub.viewModel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: DataStoreViewModel
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimate()
        Handler(Looper.getMainLooper()).postDelayed({
            auth()
        }, 3000)
    }

    private fun playAnimate() {
        val imgAlpha = ObjectAnimator.ofFloat(binding.imgLogo, View.ALPHA, 1F).setDuration(1000)
        val titleApp = ObjectAnimator.ofFloat(binding.tvAppName, View.TRANSLATION_Y, 0F, -50F)
            .setDuration(1500)

        AnimatorSet().apply {
            playTogether(imgAlpha, titleApp)
            start()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun auth() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPref.getInstance(dataStore))
        )[DataStoreViewModel::class.java]
        viewModel.getUserLogin().observe(this) { login ->
            if (login.token.isEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        }
    }
}