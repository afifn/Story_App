package com.afifny.storysub.ui.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.afifny.storysub.data.Result
import com.afifny.storysub.databinding.ActivityRegisterBinding
import com.afifny.storysub.ui.login.LoginActivity
import com.afifny.storysub.viewModel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupViewModel()
    }

    private fun setupAction() {
        binding.tvSignIn.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java),
                ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
            )
        }
        binding.btnRegister.setOnClickListener { register() }
    }

    private fun setupViewModel() {
        registerViewModel =
            ViewModelProvider(this, MainViewModelFactory(this))[RegisterViewModel::class.java]
    }

    private fun showLoading(it: Boolean) {
        if (it) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnRegister.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnRegister.isEnabled = true
        }
    }

    @SuppressLint("ShowToast")
    private fun register() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        when {
            name.isEmpty() -> {
                binding.edRegisterName.error = "Input name"
            }
            email.isEmpty() -> {
                binding.edRegisterEmail.error = "Input email"
            }
            password.isEmpty() -> {
                binding.edRegisterPassword.error = "Input password"
            }
            else -> {
                registerViewModel.userRegister(name, email, password).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            val message = result.data?.message
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Snackbar.make(binding.root, result.error, Snackbar.LENGTH_SHORT).show()
                        }
                        else -> {
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }
}