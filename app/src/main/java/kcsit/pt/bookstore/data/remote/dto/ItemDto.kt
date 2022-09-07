package kcsit.pt.bookstore.data.remote.dto

import com.google.gson.annotations.SerializedName
import kcsit.pt.bookstore.data.cache.entity.BookEntity
import kcsit.pt.bookstore.domain.model.*

data class ItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("saleInfo")
    val saleInfo: SaleInfoDto?,
    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfoDto?,
) {
    fun toBook() = Book(
        id = id,
        saleInfo = saleInfo?.toSaleInfo() ?: SaleInfo(
            "",
            ListPrice(0.0, "")
        ),
        volumeInfo = volumeInfo?.toVolumeInfo() ?: VolumeInfo(
            emptyList(),
            "",
            ImageLinks("", ""),
            "",
            ""
        )
    )

    fun toBookEntity() = BookEntity(
        bookId = id,
        buyLink = saleInfo?.buyLink ?: "",
        amount = saleInfo?.listPrice?.amount ?: 0.0,
        currencyCode = saleInfo?.listPrice?.currencyCode ?: "",
        thumbnail = volumeInfo?.imageLinks?.thumbnail ?: "",
        description = volumeInfo?.description ?: "",
        subtitle = volumeInfo?.subtitle ?: "",
        title = volumeInfo?.title ?: ""
    )
}