package com.project.booksnippets.network.api

import com.project.booksnippets.data.GoogleBookModel
import retrofit2.http.*

interface BookInfoApi {
    @GET("/books/v1/volumes")
    suspend fun evaluation(
        @QueryMap(encoded=true) filters: Map<String, String>
    ): GoogleBookModel

    companion object {
        var BASE_URL = "https://www.googleapis.com/"
    }
}