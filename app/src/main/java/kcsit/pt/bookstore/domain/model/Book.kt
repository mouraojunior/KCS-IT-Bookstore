package kcsit.pt.bookstore.domain.model

data class Book(
    val id: String,
    val saleInfo: SaleInfo,
    val volumeInfo: VolumeInfo,
)