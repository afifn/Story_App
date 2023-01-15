package com.afifny.storysub.ui.main.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifny.storysub.adapter.LoadingStateAdapter
import com.afifny.storysub.adapter.StoryAdapter
import com.afifny.storysub.data.remote.response.ListStoryItem
import com.afifny.storysub.databinding.FragmentHomeBinding
import com.afifny.storysub.ui.main.detail.StoryDetailActivity
import com.afifny.storysub.viewModel.MainViewModelFactory

class HomeFragment : Fragment(), StoryAdapter.OnClickItem, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: StoryAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentHomeBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StoryAdapter()
        setupViewModel()
        setupRC()
        setupRefreshLayout()
    }

    private fun setupRefreshLayout() {
        binding.swipe.setOnRefreshListener(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(requireContext())
        )[MainViewModel::class.java]
        viewModel.story.observe(viewLifecycleOwner) { data ->
            adapter.submitData(lifecycle, data)
        }
    }

    private fun setupRC() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        adapter.setOnClick(this)
        adapter.addLoadStateListener { loadStates ->
            if ((loadStates.source.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached && adapter.itemCount < 1) || loadStates.source.refresh is LoadState.Error) {
                binding.apply {
                    recyclerView.isVisible = false
                    viewError.root.isVisible = true
                }
            } else {
                binding.apply {
                    recyclerView.isVisible = true
                    viewError.root.isVisible = false
                }
            }
            binding.swipe.isRefreshing = loadStates.source.refresh is LoadState.Loading
        }
    }

    override fun onClickItem(story: ListStoryItem, optionsCompat: ActivityOptionsCompat) {
        val intent = Intent(requireContext(), StoryDetailActivity::class.java)
        intent.putExtra(StoryDetailActivity.EXTRA_DATA, story)
        startActivity(intent, optionsCompat.toBundle())
    }

    override fun onStart() {
        super.onStart()
        setupViewModel()
    }

    override fun onRefresh() {
        binding.swipe.isRefreshing = false
        adapter.refresh()
    }
}