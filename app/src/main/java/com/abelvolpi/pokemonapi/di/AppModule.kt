package com.abelvolpi.pokemonapi.di

import com.abelvolpi.pokemonapi.data.api.retrofit.ApiProvider
import com.abelvolpi.pokemonapi.data.api.services.PokemonService
import com.abelvolpi.pokemonapi.data.repository.PokemonRepositoryImpl
import com.abelvolpi.pokemonapi.domain.usecase.GetPokemonListUseCase
import com.abelvolpi.pokemonapi.domain.repository.PokemonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providePokemonService(): PokemonService {
        return ApiProvider.retrofit.create(PokemonService::class.java)
    }

    @Provides
    fun providePokemonRepository(
        pokemonService: PokemonService
    ): PokemonRepository {
        return PokemonRepositoryImpl(pokemonService)
    }

    @Provides
    fun provideGetPokemonListUseCase(
        repository: PokemonRepository
    ): GetPokemonListUseCase {
        return GetPokemonListUseCase(repository)
    }
}