package kcsit.pt.bookstore.domain.model

data class Item(
    val id: String,
    val saleInfo: SaleInfo,
    val volumeInfo: VolumeInfo,
)