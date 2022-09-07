package kcsit.pt.bookstore.data.cache.dao

import androidx.room.*
import kcsit.pt.bookstore.data.cache.BookWithAuthors
import kcsit.pt.bookstore.data.cache.entity.AuthorEntity
import kcsit.pt.bookstore.data.cache.entity.BookAuthorCrossRefEntity
import kcsit.pt.bookstore.data.cache.entity.BookEntity

@Dao
interface BookstoreDAO {
    @Transaction
    @Query("SELECT * FROM tbBook")
    fun getBooksWithAuthors(): List<BookWithAuthors>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthors(authors: List<AuthorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooksAuthorsCrossRef(booksAuthorsCrossRef: List<BookAuthorCrossRefEntity>)

    @Query("DELETE FROM tbBook")
    suspend fun clearBooks()
}