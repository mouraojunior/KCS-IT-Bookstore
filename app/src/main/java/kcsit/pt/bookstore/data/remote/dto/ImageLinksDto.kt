package kcsit.pt.bookstore.data.remote.dto

import com.google.gson.annotations.SerializedName
import kcsit.pt.bookstore.domain.model.ImageLinks

data class ImageLinksDto(
    @SerializedName("smallThumbnail")
    val smallThumbnail: String?,
    @SerializedName("thumbnail")
    val thumbnail: String?,
) {
    fun toImageLinks() = ImageLinks(
        smallThumbnail = smallThumbnail ?: "",
        thumbnail = thumbnail ?: ""
    )
}