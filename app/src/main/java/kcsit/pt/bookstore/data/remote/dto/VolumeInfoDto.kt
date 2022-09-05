package kcsit.pt.bookstore.data.remote.dto

import com.google.gson.annotations.SerializedName
import kcsit.pt.bookstore.domain.model.ImageLinks
import kcsit.pt.bookstore.domain.model.VolumeInfo

data class VolumeInfoDto(
    @SerializedName("authors")
    val authors: List<String>?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("imageLinks")
    val imageLinks: ImageLinksDto?,
    @SerializedName("subtitle")
    val subtitle: String?,
    @SerializedName("title")
    val title: String?,
) {
    fun toVolumeInfo() = VolumeInfo(
        authors = authors ?: emptyList(),
        description = description ?: "",
        imageLinks = imageLinks?.toImageLinks() ?: ImageLinks("", ""),
        subtitle = subtitle ?: "",
        title = title ?: ""
    )
}