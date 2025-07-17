package com.realworld.android.petsave.common.domain.repositories

import com.realworld.android.petsave.common.domain.model.animal.Animal
import com.realworld.android.petsave.common.domain.model.animal.details.AnimalWithDetails
import io.reactivex.Flowable
import com.realworld.android.petsave.common.domain.model.pagination.PaginatedAnimals

interface AnimalRepository {
    fun getAnimals(): Flowable<List<Animal>>
    suspend fun requestMoreAnimals(pageToLoad: Int, numberOfItems: Int): PaginatedAnimals
    suspend fun storeAnimals(animals: List<AnimalWithDetails>)

}