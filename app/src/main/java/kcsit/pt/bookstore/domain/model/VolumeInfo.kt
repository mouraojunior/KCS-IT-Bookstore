package kcsit.pt.bookstore.domain.model

data class VolumeInfo(
    val authors: List<String>,
    val description: String,
    val imageLinks: ImageLinks,
    val subtitle: String,
    val title: String
)