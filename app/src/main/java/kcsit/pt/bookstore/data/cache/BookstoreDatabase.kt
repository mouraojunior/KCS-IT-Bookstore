package kcsit.pt.bookstore.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import kcsit.pt.bookstore.data.cache.dao.BookstoreDAO
import kcsit.pt.bookstore.data.cache.dao.RemoteKeyDao
import kcsit.pt.bookstore.data.cache.entity.AuthorEntity
import kcsit.pt.bookstore.data.cache.entity.BookAuthorCrossRefEntity
import kcsit.pt.bookstore.data.cache.entity.BookEntity
import kcsit.pt.bookstore.data.cache.entity.RemoteKeyEntity

@Database(entities = [BookEntity::class, AuthorEntity::class, BookAuthorCrossRefEntity::class,
    RemoteKeyEntity::class], version = 1)
abstract class BookstoreDatabase : RoomDatabase() {
    abstract val bookstoreDao: BookstoreDAO
    abstract val remoteKeyDao: RemoteKeyDao
}