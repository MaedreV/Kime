package com.karate.kime.data.youtube

import com.karate.kime.data.youtube.YouTubeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeService {
    @GET("videos")
    suspend fun getVideos(
        @Query("part") part: String = "snippet,contentDetails",
        @Query("id") id: String,
        @Query("key") key: String
    ): YouTubeResponse
}
