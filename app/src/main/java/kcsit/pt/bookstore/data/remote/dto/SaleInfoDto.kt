package kcsit.pt.bookstore.data.remote.dto

import com.google.gson.annotations.SerializedName
import kcsit.pt.bookstore.domain.model.ListPrice
import kcsit.pt.bookstore.domain.model.SaleInfo

data class SaleInfoDto(
    @SerializedName("listPrice")
    val listPrice: ListPriceDto?,
    @SerializedName("buyLink")
    val buyLink: String?,
) {
    fun toSaleInfo() = SaleInfo(
        buyLink = buyLink ?: "",
        listPrice = listPrice?.toListPrice() ?: ListPrice(
            0.0,
            ""
        ))
}