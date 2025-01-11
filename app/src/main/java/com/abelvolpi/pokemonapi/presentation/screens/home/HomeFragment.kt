package com.abelvolpi.pokemonapi.presentation.screens.home

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abelvolpi.pokemonapi.presentation.adapters.HomeAdapter
import com.abelvolpi.pokemonapi.presentation.adapters.SpacesItemDecoration
import com.abelvolpi.pokemonapi.presentation.UiState
import com.abelvolpi.pokemonapi.databinding.FragmentHomeBinding
import com.abelvolpi.pokemonapi.presentation.models.CustomImage
import com.abelvolpi.pokemonapi.data.models.GenericPokemon
import com.abelvolpi.pokemonapi.presentation.screens.BaseFragment
import com.abelvolpi.pokemonapi.utils.Constants.LIMIT_PER_REQUEST
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var pokemonAdapter: HomeAdapter
    private val navController by lazy {
        findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation

        sendRequest(homeViewModel.offSet)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!::pokemonAdapter.isInitialized)
            pokemonAdapter =
                HomeAdapter(context = requireContext(), onPokemonClick = ::onPokemonClick)

        initLayout()
        observePokemonListState()

        postponeEnterTransition()
        binding.gridRecyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun initLayout() {
        with(binding) {
            val spacingInPixels = 30
            gridRecyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))

            gridRecyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = pokemonAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                        if (dy > 0) {
                            val visibleCount = (layoutManager as GridLayoutManager).childCount
                            val totalItemCount = (layoutManager as GridLayoutManager).itemCount
                            val pastVisibleItems =
                                (layoutManager as GridLayoutManager).findFirstVisibleItemPosition()

                            if (homeViewModel.pokemonListState.value != UiState.Loading &&
                                (visibleCount + pastVisibleItems) >= totalItemCount
                            ) {
                                sendRequest(homeViewModel.offSet)
                            }
                            super.onScrolled(recyclerView, dx, dy)
                        }
                    }
                })
            }
        }
    }

    private fun observePokemonListState() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.pokemonListState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.homeProgressBar.visibility = View.VISIBLE
                        binding.gridRecyclerView.visibility = View.GONE
                    }

                    is UiState.Success -> {
                        binding.homeProgressBar.visibility = View.GONE
                        binding.gridRecyclerView.visibility = View.VISIBLE
                        pokemonAdapter.addMorePokemon(state.data.results)
                        homeViewModel.offSet += LIMIT_PER_REQUEST
                    }

                    is UiState.Failure -> {
                        binding.homeProgressBar.visibility = View.GONE
                        binding.gridRecyclerView.visibility = View.VISIBLE
                        Log.e("HOME_ERROR", state.exception.toString())
                    }
                }
            }
        }
    }

    private fun sendRequest(offSet: Int) {
        homeViewModel.fetchPokemonList(offSet, LIMIT_PER_REQUEST)
    }

    private fun onPokemonClick(
        genericPokemon: GenericPokemon?,
        pokemonImageBitmap: CustomImage?,
        imageView: ImageView
    ) {
        val extras = FragmentNavigatorExtras(
            imageView to genericPokemon?.number!!
        )
        navController.navigate(
            HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                pokemonImageBitmap,
                genericPokemon
            ),
            extras
        )
    }
}
