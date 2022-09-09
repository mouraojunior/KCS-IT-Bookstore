package kcsit.pt.bookstore.presentation.book_details

sealed class BookDetailsEvent {
    data class GetBookById(val hasInternetConnection: Boolean, val bookId: String) : BookDetailsEvent()
    data class UpdateFavoriteBook(val isFavorite: Boolean, val bookId: String) : BookDetailsEvent()
}