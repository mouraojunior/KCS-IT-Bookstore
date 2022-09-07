package kcsit.pt.bookstore.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbAuthor")
data class AuthorEntity(
    @PrimaryKey val authorNameId: String,
)