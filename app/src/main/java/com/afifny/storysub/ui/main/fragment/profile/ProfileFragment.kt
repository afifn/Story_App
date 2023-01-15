package com.afifny.storysub.ui.main.fragment.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.afifny.storysub.R
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.databinding.FragmentProfileBinding
import com.afifny.storysub.ui.login.LoginActivity
import com.afifny.storysub.viewModel.DataStoreViewModel
import com.afifny.storysub.viewModel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: DataStoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupAction()
        setupView()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPref.getInstance(requireContext().dataStore))
        )[DataStoreViewModel::class.java]
    }

    private fun setupView() {
        viewModel.getUserLogin().observe(viewLifecycleOwner) { user ->
            binding.tvName.text = user.name
        }
    }

    private fun setupAction() {
        binding.btnSetting.setOnClickListener { setting() }
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.are_you_sure))
            .setMessage(getString(R.string.message_dialog_logout))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.logout()
                startActivity(Intent(activity, LoginActivity::class.java))
                Toast.makeText(
                    requireContext(),
                    getString(R.string.logout_success),
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setting() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }
}