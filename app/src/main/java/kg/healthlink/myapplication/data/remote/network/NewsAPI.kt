package kg.healthlink.myapplication.data.remote.network

import kg.healthlink.myapplication.BuildConfig
import kg.healthlink.myapplication.data.model.news.NewsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("everything")
    suspend fun getEverything(
        @Query("q")
        q: String?,
        @Query("apiKey")
        apiKey: String = BuildConfig.API_KEY,
    ): Response<NewsModel>

//    @GET("top-headlines")
//    suspend fun topHeadlines(
//        @Query("country")
//        country: String?,
//        @Query("category")
//        category: String?,
//        @Query("apiKey")
//        apiKey: String = BuildConfig.API_KEY,
//    ): Response<NewsModel>

}