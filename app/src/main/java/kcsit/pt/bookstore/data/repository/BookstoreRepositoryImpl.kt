package kcsit.pt.bookstore.data.repository

import kcsit.pt.bookstore.data.cache.dao.BookstoreDAO
import kcsit.pt.bookstore.data.cache.entity.AuthorEntity
import kcsit.pt.bookstore.data.cache.entity.BookAuthorCrossRefEntity
import kcsit.pt.bookstore.data.remote.BookstoreApi
import kcsit.pt.bookstore.domain.model.Book
import kcsit.pt.bookstore.domain.repository.BookstoreRepository
import kcsit.pt.bookstore.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class BookstoreRepositoryImpl @Inject constructor(
    private val bookstoreApi: BookstoreApi,
    private val bookstoreDAO: BookstoreDAO,
) : BookstoreRepository {
    override suspend fun getVolumes(
        hasInternetConnection: Boolean,
        isFilterActive: Boolean,
        query: String,
        maxResults: Int,
        startIndex: Int,
    ): Flow<Resource<List<Book>>> =
        flow {
            emit(Resource.Loading())
            if (isFilterActive) emit(Resource.Success(bookstoreDAO.getBooksWithAuthors()
                .filter { it.book.isFavorite }
                .map { it.toBook() }))
            else emit(Resource.Success(bookstoreDAO.getBooksWithAuthors().map { it.toBook() }))
            if (hasInternetConnection) {
                try {
                    val response = bookstoreApi.getVolumes(
                        query = query,
                        maxResults = maxResults,
                        startIndex = startIndex)

                    val responseBodyItems = response.body()?.items ?: emptyList()
                    val books = responseBodyItems.filter {
                        (it.volumeInfo?.authors?.isNotEmpty() ?: false
                                && it.volumeInfo?.imageLinks?.thumbnail?.isNotEmpty() ?: false)
                    }

                    val authors = mutableListOf<String>()
                    val booksAuthorsCrossRef = mutableListOf<BookAuthorCrossRefEntity>()

                    books.forEach { bookItemDto ->
                        bookItemDto.volumeInfo?.authors?.forEach { author ->
                            authors.add(author)
                            booksAuthorsCrossRef.add(BookAuthorCrossRefEntity(
                                bookItemDto.id,
                                author
                            ))
                        }
                    }

                    books.let { bookItemListDto ->
                        bookstoreDAO.apply {
                            insertBooks(books = bookItemListDto.map { it.toBookEntity() })
                            insertAuthors(authors = authors.map { AuthorEntity(it) })
                            insertBooksAuthorsCrossRef(booksAuthorsCrossRef = booksAuthorsCrossRef)
                        }
                        if (isFilterActive) emit(Resource.Success(bookstoreDAO.getBooksWithAuthors()
                            .filter { it.book.isFavorite }
                            .map { it.toBook() }))
                        else emit(Resource.Success(bookstoreDAO.getBooksWithAuthors().map { it.toBook() }))
                    }
                } catch (e: Exception) {
                    Timber.e("${e.message}\n${e.localizedMessage}\n${e.printStackTrace()}")
                    emit(Resource.Error("An unexpected error has occurred."))
                }
            }
        }

    override suspend fun getVolumeById(
        hasInternetConnection: Boolean,
        bookId: String,
    ): Flow<Resource<Book>> =
        flow {
            emit(Resource.Loading())
            emit(Resource.Success(bookstoreDAO.getBookWithAuthorsByBookId(bookId).toBook()))
            if (hasInternetConnection) {
                try {
                    val response = bookstoreApi.getVolumeById(
                        volumeId = bookId)

                    val responseBodyBook = response.body()
                    val authors = mutableListOf<String>()
                    val booksAuthorsCrossRef = mutableListOf<BookAuthorCrossRefEntity>()

                    responseBodyBook?.volumeInfo?.authors?.forEach { author ->
                        authors.add(author)
                        booksAuthorsCrossRef.add(BookAuthorCrossRefEntity(
                            responseBodyBook.id,
                            author
                        ))
                    }

                    bookstoreDAO.apply {
                        responseBodyBook?.let {
                            insertBooks(books = listOf(it.toBookEntity()))
                        }
                        insertAuthors(authors = authors.map { AuthorEntity(it) })
                        insertBooksAuthorsCrossRef(booksAuthorsCrossRef = booksAuthorsCrossRef)
                    }
                    emit(Resource.Success(bookstoreDAO.getBookWithAuthorsByBookId(bookId).toBook()))
                } catch (e: Exception) {
                    Timber.e("${e.message}\n${e.localizedMessage}\n${e.printStackTrace()}")
                    emit(Resource.Error("An unexpected error has occurred."))
                }
            }
        }

    override suspend fun updateFavoriteBook(isFavorite: Boolean, bookId: String) {
        bookstoreDAO.updateFavoriteBook(isFavorite = isFavorite, bookId = bookId)
    }
}