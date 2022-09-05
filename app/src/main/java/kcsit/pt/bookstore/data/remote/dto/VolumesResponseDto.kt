package kcsit.pt.bookstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VolumesResponseDto(
    @SerializedName("items")
    val items: List<ItemDto>?,
    @SerializedName("totalItems")
    val totalItems: Int?,
)