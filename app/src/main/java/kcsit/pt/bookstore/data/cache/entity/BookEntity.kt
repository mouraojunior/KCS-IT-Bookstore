package kcsit.pt.bookstore.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbBook")
data class BookEntity(
    @PrimaryKey val bookId: String,
    val buyLink: String,
    val amount: Double,
    val currencyCode: String,
    val thumbnail: String,
    val description: String,
    val subtitle: String,
    val title: String,
    val isFavorite: Boolean = false,
)