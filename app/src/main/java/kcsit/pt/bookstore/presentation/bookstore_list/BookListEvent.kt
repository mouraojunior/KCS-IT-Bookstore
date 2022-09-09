package kcsit.pt.bookstore.presentation.bookstore_list

sealed class BookListEvent {
    data class GetBooks(val hasInternetConnection: Boolean, val isFilterActive: Boolean) : BookListEvent()
    data class SetFilter(val isFilterActive: Boolean) : BookListEvent()
}