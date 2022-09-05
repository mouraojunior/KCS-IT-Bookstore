package kcsit.pt.bookstore.data.repository

import kcsit.pt.bookstore.data.remote.BookstoreApi
import kcsit.pt.bookstore.domain.model.Item
import kcsit.pt.bookstore.domain.repository.BookstoreRepository
import kcsit.pt.bookstore.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class BookstoreRepositoryImpl @Inject constructor(
    private val bookstoreApi: BookstoreApi,
) : BookstoreRepository {
    override suspend fun getVolumes(
        query: String,
        maxResults: Int,
        startIndex: Int,
    ): Flow<Resource<List<Item>>> =
        flow {
            emit(Resource.Loading())
            try {
                val response = bookstoreApi.getVolumes(
                    query = query,
                    maxResults = maxResults,
                    startIndex = startIndex)

                val responseBody = response.body()
                val books = responseBody?.items

                if (books == null || books.isEmpty())
                    emit(Resource.Success(emptyList()))
                else emit(Resource.Success(books.map { it.toItem() }.filter { it.volumeInfo.authors.isNotEmpty() }))

            } catch (e: Exception) {
                Timber.e("${e.message}\n${e.localizedMessage}\n${e.printStackTrace()}")
                emit(Resource.Error("An unexpected error has occurred."))
            }
        }
}