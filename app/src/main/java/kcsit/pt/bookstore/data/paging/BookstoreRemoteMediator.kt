package kcsit.pt.bookstore.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import kcsit.pt.bookstore.data.cache.BookWithAuthors
import kcsit.pt.bookstore.data.cache.BookstoreDatabase
import kcsit.pt.bookstore.data.cache.entity.AuthorEntity
import kcsit.pt.bookstore.data.cache.entity.BookAuthorCrossRefEntity
import kcsit.pt.bookstore.data.cache.entity.RemoteKeyEntity
import kcsit.pt.bookstore.data.remote.BookstoreApi
import kcsit.pt.bookstore.data.remote.dto.VolumesResponseDto
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalPagingApi
class BookstoreRemoteMediator(
    private val bookstoreApi: BookstoreApi,
    val db: BookstoreDatabase,
    private val hasInternetConnection: Boolean,
    private val query: String,
    private val maxResults: Int,
) : RemoteMediator<Int, BookWithAuthors>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, BookWithAuthors>,
    ): MediatorResult {
        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> return pageKeyData
            else -> pageKeyData as Int
        }

        try {
            var response: Response<VolumesResponseDto>? = null
            if (hasInternetConnection)
                response = bookstoreApi.getVolumes(
                    query = query,
                    maxResults = maxResults,
                    startIndex = page
                )

            val responseBodyItems = response?.body()?.items ?: emptyList()
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

            val body = response?.body()
            val isEndOfList = body?.items == null
                    || body.items.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) db.remoteKeyDao.deleteAll()
                val prevKey = if (page == 0) null else page - 30
                val nextKey = if (isEndOfList) null else page + 30
                val keys = books.map {
                    RemoteKeyEntity(it.id, prevKey = prevKey, nextKey = nextKey)
                }

                db.bookstoreDao.apply {
                    insertBooks(books = books.map { it.toBookEntity() })
                    insertAuthors(authors = authors.map { AuthorEntity(it) })
                    insertBooksAuthorsCrossRef(booksAuthorsCrossRef = booksAuthorsCrossRef)
                }
                db.remoteKeyDao.insertAll(keys)
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, BookWithAuthors>,
    ): Any {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 0
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey
                return nextKey ?: MediatorResult.Success(endOfPaginationReached = false)
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = false
                )
            }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, BookWithAuthors>): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.toBook()?.id?.let { repoId ->
                db.remoteKeyDao.remoteKeysBookId(repoId)
            }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, BookWithAuthors>): RemoteKeyEntity? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { bookWithAuthors ->
                val book = bookWithAuthors.toBook()
                db.remoteKeyDao.remoteKeysBookId(book.id)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, BookWithAuthors>): RemoteKeyEntity? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { bookWithAuthors ->
                val book = bookWithAuthors.toBook()
                db.remoteKeyDao.remoteKeysBookId(book.id)
            }
    }
}