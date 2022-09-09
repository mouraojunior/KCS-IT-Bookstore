package kcsit.pt.bookstore.data.cache.entity

import androidx.room.Entity

@Entity(tableName = "tbBookAuthorCrossRef", primaryKeys = ["bookId", "authorNameId"])
data class BookAuthorCrossRefEntity(
    val bookId: String,
    val authorNameId: String,
)