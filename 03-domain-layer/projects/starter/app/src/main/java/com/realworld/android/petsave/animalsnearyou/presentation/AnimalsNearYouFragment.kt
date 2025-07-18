package com.realworld.android.petsave.animalsnearyou.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.realworld.android.petsave.R
import com.realworld.android.petsave.common.presentation.AnimalsAdapter
import com.realworld.android.petsave.common.presentation.Event
import com.realworld.android.petsave.databinding.FragmentAnimalsNearYouBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnimalsNearYouFragment : Fragment() {

    companion object {
        private const val ITEMS_PER_ROW = 2
    }

    private val binding get() = _binding!!

    private var _binding: FragmentAnimalsNearYouBinding? = null
    private val viewModel: AnimalsNearYouFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnimalsNearYouBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        requestInitialAnimalsList()
    }

    private fun setupUI() {
        val adapter = createAdapter()
        setupRecyclerView(adapter)
        subscribeToViewStateUpdates(adapter)
    }

    private fun createAdapter(): AnimalsAdapter {
        return AnimalsAdapter()
    }

    private fun setupRecyclerView(animalsNearYouAdapter: AnimalsAdapter) {
        binding.animalsRecyclerView.apply {
            adapter = animalsNearYouAdapter
            layoutManager = GridLayoutManager(requireContext(), ITEMS_PER_ROW)
            setHasFixedSize(true)
            addOnScrollListener(createInfiniteScrollListener(layoutManager as GridLayoutManager))
        }
    }

    private fun createInfiniteScrollListener(
        layoutManager: GridLayoutManager
    ): RecyclerView.OnScrollListener {
        return object: InfiniteScrollListener(
            layoutManager,
            AnimalsNearYouFragmentViewModel.UI_PAGE_SIZE
        ){
            override fun loadMoreItems() {
                requestMoreAnimals()
            }

            override fun isLastPage(): Boolean {
                return viewModel.isLoadingMoreAnimals
            }

            override fun isLoading(): Boolean {
                return viewModel.isLastPage
            }

        }
    }

    private fun requestMoreAnimals() {
        viewModel.onEvent(AnimalsNearYouEvent.RequestMoreAnimals)
    }

    private fun subscribeToViewStateUpdates(adapter: AnimalsAdapter) {
        //you call launch on viewLifecycleOwner so the coroutine gets scoped to the View.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    updateScreenState(it, adapter)
                }
            }
        }
    }

    private fun updateScreenState(
        state: AnimalsNearYouViewState,
        adapter: AnimalsAdapter
    ) {
        // 1
        binding.progressBar.isVisible = state.loading
        adapter.submitList(state.animals)
        handleNoMoreAnimalsNearby(state.noMoreAnimalsNearby)
        handleFailures(state.failure)
    }

    private fun handleNoMoreAnimalsNearby(
        noMoreAnimalsNearby: Boolean
    ) {
    }

    private fun handleFailures(failure: Event<Throwable>?) {
        val unhandledFailure = failure?.getContentIfNotHandled() ?: return

        val fallbackMessage = getString(R.string.an_error_occurred)

        val snackBarMessage = if (unhandledFailure.message.isNullOrEmpty()) {
            fallbackMessage
        } else {
            unhandledFailure.message!!
        }
        if (snackBarMessage.isNotEmpty()) {
            Snackbar.make(requireView(), snackBarMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun requestInitialAnimalsList() {
        viewModel.onEvent(AnimalsNearYouEvent.RequestInitialAnimalList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}