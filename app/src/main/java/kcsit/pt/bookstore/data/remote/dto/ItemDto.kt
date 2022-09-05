package kcsit.pt.bookstore.data.remote.dto

import com.google.gson.annotations.SerializedName
import kcsit.pt.bookstore.domain.model.*

data class ItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("saleInfo")
    val saleInfo: SaleInfoDto?,
    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfoDto?,
) {
    fun toItem() = Item(
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
}