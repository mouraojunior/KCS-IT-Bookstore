package kcsit.pt.bookstore.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val bookId: String,
    val prevKey: Int?,
    val nextKey: Int?,
)
