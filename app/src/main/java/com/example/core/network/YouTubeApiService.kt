package com.example.core.network

import com.example.core.model.YouTubeChannelListResponse
import com.example.core.model.YouTubeSearchResponse
import com.example.core.model.YouTubeVideoListResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface YouTubeApiService {

    @GET("channels")
    suspend fun getChannelDetails(
        @Query("part") part: String = "snippet,statistics,contentDetails",
        @Query("id") channelId: String? = null,
        @Query("forHandle") forHandle: String? = null,
        @Query("key") apiKey: String
    ): YouTubeChannelListResponse

    @GET("search")
    suspend fun searchChannelVideos(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("order") order: String = "date",
        @Query("type") type: String = "video",
        @Query("key") apiKey: String
    ): YouTubeSearchResponse

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "snippet,statistics,contentDetails",
        @Query("id") videoIds: String,
        @Query("key") apiKey: String
    ): YouTubeVideoListResponse

    @GET("videos")
    suspend fun getVideoStatistics(
        @Query("part") part: String = "statistics,contentDetails,snippet",
        @Query("id") videoIds: String,
        @Query("key") apiKey: String
    ): YouTubeVideoListResponse

    @GET("search")
    suspend fun searchTrendingNiche(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("order") order: String = "viewCount",
        @Query("type") type: String = "video",
        @Query("key") apiKey: String
    ): YouTubeSearchResponse
}

object YouTubeNetworkClient {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val api: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(YouTubeApiService::class.java)
    }

    fun parseIsoDurationToSeconds(duration: String?): Int {
        if (duration.isNullOrBlank()) return 300
        return try {
            var d = duration.removePrefix("PT")
            var hours = 0
            var minutes = 0
            var seconds = 0
            if (d.contains("H")) {
                val parts = d.split("H")
                hours = parts[0].toIntOrNull() ?: 0
                d = parts.getOrNull(1) ?: ""
            }
            if (d.contains("M")) {
                val parts = d.split("M")
                minutes = parts[0].toIntOrNull() ?: 0
                d = parts.getOrNull(1) ?: ""
            }
            if (d.contains("S")) {
                seconds = d.removeSuffix("S").toIntOrNull() ?: 0
            }
            hours * 3600 + minutes * 60 + seconds
        } catch (e: Exception) {
            300
        }
    }
}
