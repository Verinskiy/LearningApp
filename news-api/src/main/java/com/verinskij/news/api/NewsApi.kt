package com.verinskij.news.api

import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import com.verinskij.news.api.model.ArticleDTO
import com.verinskij.news.api.model.Language
import com.verinskij.news.api.model.ResponseDTO
import com.verinskij.news.api.utils.NewsApiKeyInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("everything")
    suspend fun everything(
        @Query("q") query: String,
        @Query("language") language: List<@JvmSuppressWildcards Language>? = null,
        @Query("pageSize") pageSize: Int = MAX_PAGE_SIZE,
        @Query("page") page: Int = 1,
    ): Result<ResponseDTO<ArticleDTO>>

    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("page") page: Int = 1,
    ): Result<ResponseDTO<ArticleDTO>>

    companion object {
        const val MAX_PAGE_SIZE = 100
    }
}

fun NewsApi(
    baseUrl: String,
    apiKey: String,
    okHttpClient: OkHttpClient? = null
): NewsApi {

    val httpClient = (okHttpClient?.newBuilder() ?: OkHttpClient.Builder())
        .addInterceptor(NewsApiKeyInterceptor(apiKey))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(ResultCallAdapterFactory.create())
        .client(httpClient)
        .build()

    return retrofit.create(NewsApi::class.java)
}