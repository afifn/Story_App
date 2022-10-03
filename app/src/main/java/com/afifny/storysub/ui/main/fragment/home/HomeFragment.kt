package com.afifny.storysub.ui.main.fragment.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afifny.storysub.adapter.StoryAdapter
import com.afifny.storysub.databinding.FragmentHomeBinding
import com.afifny.storysub.model.ListStoryItem
import com.afifny.storysub.model.UserPref
import com.afifny.storysub.ui.main.MainViewModel
import com.afifny.storysub.ui.main.detail.StoryDetailActivity
import com.afifny.storysub.viewModel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class HomeFragment : Fragment(), StoryAdapter.OnClickItem {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StoryAdapter()
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory(UserPref.getInstance(requireContext().dataStore)))[MainViewModel::class.java]
        viewModel.getUserLogin().observe(viewLifecycleOwner) {
            user ->
            if (user.token.isNotEmpty()){
                viewModel.getListStory(user.token)
            }
        }
        viewModel.isLoad.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        viewModel.listStory.observe(viewLifecycleOwner) {
            list ->
            setupRC(list)
        }
    }

    private fun showLoading(b: Boolean?) {
        binding.progressBar.visibility = if (b == true) View.VISIBLE else View.GONE
    }

    private fun setupRC(list: List<ListStoryItem>) {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)
        adapter.setAdapter(list)
        binding.recyclerView.adapter = adapter
        adapter.setOnClick(this)
    }

     override fun onClickItem(story: ListStoryItem, position: Int, optionsCompat: ActivityOptionsCompat) {
         val intent = Intent(requireContext(), StoryDetailActivity::class.java)
         intent.putExtra(StoryDetailActivity.EXTRA_DATA, story)
         startActivity(intent, optionsCompat.toBundle())
     }
}