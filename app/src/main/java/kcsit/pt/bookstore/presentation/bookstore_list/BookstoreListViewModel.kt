package kcsit.pt.bookstore.presentation.bookstore_list

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
class BookstoreListViewModel @Inject constructor(
    private val bookstoreRepository: BookstoreRepository,
) : ViewModel() {
    private val _bookstoreItemsState = MutableSharedFlow<Resource<List<Book>>>()
    val bookstoreItemsState = _bookstoreItemsState.asSharedFlow()

    private var _isFilterActive: Boolean = false

    fun onEvent(event: BookListEvent) {
        when (event) {
            is BookListEvent.GetBooks -> getBooksVolumes(event.hasInternetConnection, event.isFilterActive)
            is BookListEvent.SetFilter -> _isFilterActive = event.isFilterActive
        }
    }

    fun isFilterActive(): Boolean = _isFilterActive

    private fun getBooksVolumes(hasInternetConnection: Boolean, isFilterActive: Boolean) {
        _isFilterActive = isFilterActive
        viewModelScope.launch(Dispatchers.IO) {
            bookstoreRepository.getVolumes(
                hasInternetConnection = hasInternetConnection,
                isFilterActive = isFilterActive).collect { bookstoreState ->
                when (bookstoreState) {
                    is Resource.Error -> {
                        _bookstoreItemsState.emit(Resource.Loading(false))
                        _bookstoreItemsState.emit(
                            Resource.Error(
                                bookstoreState.message ?: ""
                            )
                        )
                    }
                    is Resource.Loading ->
                        _bookstoreItemsState.emit(Resource.Loading(true))
                    is Resource.Success -> {
                        bookstoreState.data?.let {
                            _bookstoreItemsState.emit(Resource.Success(bookstoreState.data))
                        }
                    }
                }
            }
        }
    }
}