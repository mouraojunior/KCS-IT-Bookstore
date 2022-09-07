package kcsit.pt.bookstore.domain.repository

import kcsit.pt.bookstore.domain.model.Book
import kcsit.pt.bookstore.util.Constants
import kcsit.pt.bookstore.util.Resource
import kotlinx.coroutines.flow.Flow

interface BookstoreRepository {
    suspend fun getVolumes(
        query: String = Constants.QUERY_BOOKS_MOBILE,
        maxResults: Int = Constants.BASE_MAX_RESULTS,
        startIndex: Int = Constants.BASE_START_INDEX,
    ): Flow<Resource<List<Book>>>
}