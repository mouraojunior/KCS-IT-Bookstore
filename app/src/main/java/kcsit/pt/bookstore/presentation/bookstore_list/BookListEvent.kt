package kcsit.pt.bookstore.presentation.bookstore_list

sealed class BookListEvent {
    data class GetBooks(val hasInternetConnection: Boolean) : BookListEvent()
}