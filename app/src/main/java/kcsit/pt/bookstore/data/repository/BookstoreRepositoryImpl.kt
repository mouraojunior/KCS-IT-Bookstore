package kcsit.pt.bookstore.data.repository

import androidx.paging.*
import kcsit.pt.bookstore.data.cache.BookstoreDatabase
import kcsit.pt.bookstore.data.cache.dao.BookstoreDAO
import kcsit.pt.bookstore.data.cache.entity.AuthorEntity
import kcsit.pt.bookstore.data.cache.entity.BookAuthorCrossRefEntity
import kcsit.pt.bookstore.data.paging.BookstoreRemoteMediator
import kcsit.pt.bookstore.data.remote.BookstoreApi
import kcsit.pt.bookstore.domain.model.Book
import kcsit.pt.bookstore.domain.repository.BookstoreRepository
import kcsit.pt.bookstore.util.Resource
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@ExperimentalPagingApi
class BookstoreRepositoryImpl @Inject constructor(
    private val bookstoreApi: BookstoreApi,
    private val bookstoreDAO: BookstoreDAO,
    private val db: BookstoreDatabase,
) : BookstoreRepository {
    override suspend fun getVolumes(
        hasInternetConnection: Boolean,
        isFilterActive: Boolean,
        query: String,
        maxResults: Int,
        startIndex: Int,
    ): Flow<Resource<PagingData<Book>>> = channelFlow {
        try {
            send(Resource.Loading())
            val pagingSourceFactory = {
                if (isFilterActive) bookstoreDAO.getBooksWithAuthorsByFavorite()
                else bookstoreDAO.getBooksWithAuthors()
            }

            Pager(
                config = PagingConfig(
                    pageSize = maxResults,
                    prefetchDistance = 2,
                    maxSize = PagingConfig.MAX_SIZE_UNBOUNDED,
                    jumpThreshold = Int.MIN_VALUE,
                    enablePlaceholders = true,
                ),
                remoteMediator = BookstoreRemoteMediator(
                    bookstoreApi = bookstoreApi,
                    db = db,
                    hasInternetConnection = hasInternetConnection,
                    query = query,
                    maxResults = maxResults
                ),
                pagingSourceFactory = pagingSourceFactory
            ).flow.map { BookWithAuthorsPagingData ->
                BookWithAuthorsPagingData.map { bookWithAuthors ->
                    bookWithAuthors.toBook()
                }
            }.collect {
                send(Resource.Success(it))
            }
        } catch (e: Exception) {
            Timber.e("${e.message}\n${e.localizedMessage}\n${e.printStackTrace()}")
            send(Resource.Error("An unexpected error has occurred."))
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