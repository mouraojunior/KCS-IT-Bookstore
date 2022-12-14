package kcsit.pt.bookstore.data.remote

import kcsit.pt.bookstore.data.remote.dto.ItemDto
import kcsit.pt.bookstore.data.remote.dto.VolumesResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookstoreApi {
    @GET("volumes")
    suspend fun getVolumes(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int,
        @Query("startIndex") startIndex: Int,
    ): Response<VolumesResponseDto>

    @GET("volumes/{volumeId}")
    suspend fun getVolumeById(
        @Path("volumeId") volumeId: String,
    ): Response<ItemDto>
}