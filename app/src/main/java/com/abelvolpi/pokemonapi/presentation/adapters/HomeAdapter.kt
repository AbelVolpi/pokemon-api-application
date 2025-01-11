package com.abelvolpi.pokemonapi.presentation.adapters

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.abelvolpi.pokemonapi.databinding.HomeAdapterItemBinding
import com.abelvolpi.pokemonapi.presentation.models.CustomImage
import com.abelvolpi.pokemonapi.data.models.GenericPokemon
import com.abelvolpi.pokemonapi.utils.setImageUsingGlide

class HomeAdapter(
    private val onPokemonClick: (GenericPokemon?, CustomImage?, ImageView) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private val pokemonList = arrayListOf<GenericPokemon>()

    inner class ViewHolder(private val binding: HomeAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(genericPokemon: GenericPokemon) {
            with(binding) {
                pokemonNameTextView.text = genericPokemon.name
                pokemonImage.setImageUsingGlide(context, genericPokemon.number)
                pokemonImage.transitionName = genericPokemon.number
                itemLayout.setOnClickListener {
                    onPokemonClick.invoke(
                        genericPokemon,
                        binding.pokemonImage.drawable?.let { CustomImage(it.toBitmap()) },
                        pokemonImage
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HomeAdapterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            pokemonList[position]
        )
    }

    override fun getItemCount() = pokemonList.size

    fun addMorePokemon(newGenericPokemonList: List<GenericPokemon>) {
        pokemonList.addAll(newGenericPokemonList)
        notifyDataSetChanged()
    }
}

class SpacesItemDecoration(private val space: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = space
        outRect.bottom = space
    }
}
