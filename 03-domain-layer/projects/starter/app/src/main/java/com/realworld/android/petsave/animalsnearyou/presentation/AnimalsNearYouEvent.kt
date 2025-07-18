package com.realworld.android.petsave.animalsnearyou.presentation

sealed class AnimalsNearYouEvent {
    data object RequestInitialAnimalList: AnimalsNearYouEvent()
    data object RequestMoreAnimals: AnimalsNearYouEvent()

}