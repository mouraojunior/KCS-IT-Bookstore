package kcsit.pt.bookstore.data.cache.entity

import androidx.room.Entity

@Entity(primaryKeys = ["bookId", "authorNameId"])
data class BookAuthorCrossRefEntity(
    val bookId: String,
    val authorNameId: String,
)