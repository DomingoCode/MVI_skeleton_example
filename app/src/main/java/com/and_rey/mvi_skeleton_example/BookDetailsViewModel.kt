package com.and_rey.mvi_skeleton_example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Author: &REY
 * Created on 26/11/2025
 */
@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val fetchBookDetailsUseCase: FetchBookDetailsUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State>
        get() = _state

    val userIntent = Channel<Wish>(Channel.UNLIMITED)

    init {
        handleIntent()
    }

    private fun handleIntent() {
        userIntent
            .consumeAsFlow()
            .onEach { wish ->
                when (wish) {
                    is Wish.LoadBookDetails -> {
                        _state.update { it.copy(isLoading = true) }
                        //below may be either db or network request via usecase
                        val book = fetchBookDetailsUseCase.fetchBookByIdUseCase(wish.bookId)

                        _state.update {
                            it.copy(
                                isLoading = false,
                                bookId = book.id,
                                bookName = book.name
                            )
                        }
                    }


                    is Wish.UpdateName -> {
                        _state.update { it.copy(isLoading = true) }
                        updateBookUseCase.updateName(wish.newName)

                        _state.update {
                            it.copy(
                                isLoading = false,
                                bookName = wish.newName
                            )
                        }
                    }

                    Wish.DeleteBook -> {
                        deleteBookUseCase.deleteById(id = state.value.bookId)
                        //from here you can send command to leave a fragment,
                        // show toast or whatever you want
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    sealed class Wish {
        class LoadBookDetails(val bookId: Int) : Wish()
        class UpdateName(val newName: String) : Wish()
        object DeleteBook : Wish()
    }

    data class State(
        val bookId: Int? = null,
        val bookName: String? = null,
        val isLoading: Boolean = true
    ) {
//        companion object {
//            val IDLE = State(
//                bookId = null,
//                bookName = null,
//            )
//        }
    }
}