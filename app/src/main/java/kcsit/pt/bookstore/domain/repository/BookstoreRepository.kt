package kcsit.pt.bookstore.domain.repository

import androidx.paging.PagingData
import kcsit.pt.bookstore.domain.model.Book
import kcsit.pt.bookstore.util.Constants
import kcsit.pt.bookstore.util.Resource
import kotlinx.coroutines.flow.Flow

interface BookstoreRepository {
    suspend fun getVolumes(
        hasInternetConnection: Boolean,
        isFilterActive: Boolean,
        query: String = Constants.QUERY_BOOKS_MOBILE,
        maxResults: Int = Constants.BASE_MAX_RESULTS,
        startIndex: Int = Constants.BASE_START_INDEX,
    ): Flow<Resource<PagingData<Book>>>

    suspend fun getVolumeById(
        hasInternetConnection: Boolean,
        bookId: String,
    ): Flow<Resource<Book>>

    suspend fun updateFavoriteBook(
        isFavorite: Boolean,
        bookId: String,
    )
}