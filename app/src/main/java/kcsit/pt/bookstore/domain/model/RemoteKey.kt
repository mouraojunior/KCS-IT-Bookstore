package kcsit.pt.bookstore.domain.model

data class RemoteKey(
    val id: String,
    val nextKey: Int,
    val isEndReached: Boolean
)