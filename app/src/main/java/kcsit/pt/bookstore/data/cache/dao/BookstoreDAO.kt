package kcsit.pt.bookstore.data.cache.dao

import androidx.paging.PagingSource
import androidx.room.*
import kcsit.pt.bookstore.data.cache.BookWithAuthors
import kcsit.pt.bookstore.data.cache.entity.AuthorEntity
import kcsit.pt.bookstore.data.cache.entity.BookAuthorCrossRefEntity
import kcsit.pt.bookstore.data.cache.entity.BookEntity

@Dao
interface BookstoreDAO {
    @Transaction
    @Query("SELECT * FROM tbBook")
    fun getBooksWithAuthors(): PagingSource<Int, BookWithAuthors>

    @Transaction
    @Query("SELECT * FROM tbBook WHERE isFavorite = 1")
    fun getBooksWithAuthorsByFavorite(): PagingSource<Int, BookWithAuthors>

    @Transaction
    @Query("SELECT * FROM tbBook WHERE bookId = :bookId")
    fun getBookWithAuthorsByBookId(bookId: String): BookWithAuthors

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("Update tbBook SET isFavorite = :isFavorite WHERE bookId = :bookId")
    suspend fun updateFavoriteBook(isFavorite: Boolean, bookId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthors(authors: List<AuthorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooksAuthorsCrossRef(booksAuthorsCrossRef: List<BookAuthorCrossRefEntity>)

    @Query("DELETE FROM tbBook")
    suspend fun clearBooks()

    @Query("DELETE FROM tbAuthor")
    suspend fun clearAuthors()

    @Query("DELETE FROM tbBookAuthorCrossRef")
    suspend fun clearBookAuthorCrossRef()
}