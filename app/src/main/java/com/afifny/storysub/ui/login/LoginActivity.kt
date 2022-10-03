package com.afifny.storysub.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.afifny.storysub.R
import com.afifny.storysub.databinding.ActivityLoginBinding
import com.afifny.storysub.model.UserPref
import com.afifny.storysub.ui.main.MainActivity
import com.afifny.storysub.ui.register.RegisterActivity
import com.afifny.storysub.viewModel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupViewModel()
    }

    private fun setupAction() {
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java),
                ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }
        binding.btnLogin.setOnClickListener { login() }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPref.getInstance(dataStore)))[LoginViewModel::class.java]
        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(it: Boolean) {
        if (it) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true
        }
    }

    private fun login() {
        val email = binding.edLoginEmail.text.toString()
        val pass = binding.edLoginPassword.text.toString()

        when {
            email.isEmpty() -> {
                binding.edLoginEmail.error = getString(R.string.input_email)
            }
            pass.isEmpty() -> {
                binding.edLoginPassword.error = getString(R.string.input_password)
            }
            else -> {
                loginViewModel.userLogin(email, pass)
                loginViewModel.error.observe(this) { error ->
                    if (error) {
                        loginViewModel.message.observe(this) { message ->
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                        }
                    } else {
                        loginViewModel.users.observe(this) { user ->
                            if (user.token.isNotEmpty()) {
                                loginViewModel.login(user)
                                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(Intent(this, MainActivity::class.java))
                                this.finish()
                            }
                        }
                    }
                }
            }
        }
    }
}