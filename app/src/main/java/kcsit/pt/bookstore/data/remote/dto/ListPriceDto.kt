package kcsit.pt.bookstore.data.remote.dto

import com.google.gson.annotations.SerializedName
import kcsit.pt.bookstore.domain.model.ListPrice

data class ListPriceDto(
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("currencyCode")
    val currencyCode: String?,
) {
    fun toListPrice() = ListPrice(
        amount = amount ?: 0.0,
        currencyCode = currencyCode ?: ""
    )
}