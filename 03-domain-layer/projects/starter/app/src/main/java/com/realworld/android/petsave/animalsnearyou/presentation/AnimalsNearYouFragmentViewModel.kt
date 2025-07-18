package com.realworld.android.petsave.animalsnearyou.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realworld.android.logging.Logger
import com.realworld.android.petsave.animalsnearyou.domain.usecases.GetAnimals
import com.realworld.android.petsave.animalsnearyou.domain.usecases.RequestNextPageOfAnimals
import com.realworld.android.petsave.common.domain.model.NetworkException
import com.realworld.android.petsave.common.domain.model.NetworkUnavailableException
import com.realworld.android.petsave.common.domain.model.NoMoreAnimalsException
import com.realworld.android.petsave.common.domain.model.pagination.Pagination
import com.realworld.android.petsave.common.presentation.Event
import com.realworld.android.petsave.common.presentation.model.UIAnimal
import com.realworld.android.petsave.common.presentation.model.mappers.UiAnimalMapper
import com.realworld.android.petsave.common.utils.DispatchersProvider
import com.realworld.android.petsave.common.utils.createExceptionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AnimalsNearYouFragmentViewModel @Inject constructor(
    private val getAnimals: GetAnimals,
    private val requestNextPageOfAnimals: RequestNextPageOfAnimals,
    private val uiAnimalMapper: UiAnimalMapper,
//    private val dispatchersProvider: DispatchersProvider,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    companion object {
        const val UI_PAGE_SIZE = Pagination.DEFAULT_PAGE_SIZE
    }

    private val _state = MutableStateFlow(AnimalsNearYouViewState())
    private var _currentPage = 0

    val state = _state.asStateFlow()

    val isLastPage: Boolean get() = state.value.noMoreAnimalsNearby
    var isLoadingMoreAnimals: Boolean = false
        private set

    init {
        subscribeToAnimalUpdates()
    }

    fun onEvent(event: AnimalsNearYouEvent) {
        when (event) {
            is AnimalsNearYouEvent.RequestInitialAnimalList -> loadAnimals()
            is AnimalsNearYouEvent.RequestMoreAnimals -> loadNextAnimalPage()
        }
    }

    private fun subscribeToAnimalUpdates() {
        getAnimals()
            .map { animals ->
                animals.map(uiAnimalMapper::mapToView)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onNewAnimalList, ::onFailure)
            .addTo(compositeDisposable)
    }

    private fun onNewAnimalList(animals: List<UIAnimal>) {
        Logger.d("Got more animals!")

        val updateAnimalSet = (state.value.animals + animals).toSet()
        _state.update { oldState ->
            oldState.copy(
                loading = false,
                animals = updateAnimalSet.toList()
            )
        }
    }

    private fun loadAnimals() {
        if (state.value.animals.isEmpty()) {
            loadNextAnimalPage()
        }
    }

    private fun loadNextAnimalPage() {
        isLoadingMoreAnimals = true
        val errorMessage = "Failed to fetch nearby animals"
        val exceptionHandler = viewModelScope.createExceptionHandler(errorMessage) { onFailure(it) }
        viewModelScope.launch(exceptionHandler) {
            Logger.d("Requesting more animals.")
            val pagination = requestNextPageOfAnimals(++_currentPage)
            onPaginationInfoObtained(pagination)
            isLoadingMoreAnimals = false
        }
    }

    private fun onFailure(failure: Throwable) {
        when (failure) {
            is NetworkException,
            is NetworkUnavailableException -> {
                _state.update { oldState ->
                    oldState.copy(
                        loading = false,
                        failure = Event(failure)
                    )
                }
            }

            is NoMoreAnimalsException -> {
                _state.update { oldState ->
                    oldState.copy(
                        noMoreAnimalsNearby = true,
                        failure = Event(failure)
                    )
                }
            }
        }
    }

    private fun onPaginationInfoObtained(pagination: Pagination) {
        _currentPage = pagination.currentPage
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}