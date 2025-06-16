package com.realworld.android.petsave.common.domain.model.animal

import java.time.LocalDateTime

data class Animal(
    val id: Long,
    val name: String,
    val type: String,
    val media: Media,
    val tags: List<String>,
    val adoptionStatus: AdoptionStatus,
    val publishedAt: LocalDateTime
)