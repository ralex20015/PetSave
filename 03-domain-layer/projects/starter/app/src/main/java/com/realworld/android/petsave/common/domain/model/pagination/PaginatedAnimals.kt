package com.realworld.android.petsave.common.domain.model.pagination

import com.realworld.android.petsave.common.domain.model.animal.details.AnimalWithDetails

data class PaginatedAnimals(
    val animals: List<AnimalWithDetails>,
    val pagination: Pagination,
)