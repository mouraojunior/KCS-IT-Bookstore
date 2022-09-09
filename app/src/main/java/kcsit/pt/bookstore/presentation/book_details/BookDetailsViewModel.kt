package kcsit.pt.bookstore.presentation.book_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kcsit.pt.bookstore.domain.model.Book
import kcsit.pt.bookstore.domain.repository.BookstoreRepository
import kcsit.pt.bookstore.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val bookstoreRepository: BookstoreRepository,
) : ViewModel() {
    private val _bookDetailState = MutableSharedFlow<Resource<Book>>()
    val bookDetailState = _bookDetailState.asSharedFlow()

    fun onEvent(event: BookDetailsEvent) {
        when (event) {
            is BookDetailsEvent.GetBookById -> getBookById(
                event.hasInternetConnection,
                event.bookId
            )
            is BookDetailsEvent.UpdateFavoriteBook -> updateFavoriteBook(event.isFavorite, event.bookId)
        }
    }

    private fun updateFavoriteBook(isFavorite: Boolean, bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookstoreRepository.updateFavoriteBook(
                isFavorite = isFavorite,
                bookId = bookId
            )
        }
    }

    private fun getBookById(hasInternetConnection: Boolean, bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookstoreRepository.getVolumeById(
                hasInternetConnection = hasInternetConnection,
                bookId = bookId
            ).collect { bookDetailState ->
                when (bookDetailState) {
                    is Resource.Error -> {
                        _bookDetailState.emit(Resource.Loading(false))
                        _bookDetailState.emit(
                            Resource.Error(
                                bookDetailState.message ?: ""
                            )
                        )
                    }
                    is Resource.Loading ->
                        _bookDetailState.emit(Resource.Loading(true))
                    is Resource.Success -> {
                        bookDetailState.data?.let {
                            _bookDetailState.emit(Resource.Success(bookDetailState.data))
                        }
                    }
                }
            }
        }
    }
}