package com.verinskij.newsapp

import android.content.Context
import com.verinskij.database.ArticleDAO
import com.verinskij.database.NewsDatabase
import com.verinskij.news.api.NewsApi
import com.verinskij.news.common.AndroidLogger
import com.verinskij.news.common.AppDispatchers
import com.verinskij.news.common.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsApi(okHttpClient: OkHttpClient): NewsApi {
        return NewsApi(
            baseUrl = BuildConfig.NEWS_API_BASE_URL,
            apiKey = BuildConfig.NEWS_API_KEY,
            okHttpClient
        )
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideAppCoroutineDispatchers(): AppDispatchers {
        return AppDispatchers()
    }

    @Provides
    fun provideLogger(): Logger {
        return AndroidLogger()
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseDao {

    @Provides
    @Singleton
    fun provideNewsDatabase(
        @ApplicationContext context: Context
    ): NewsDatabase {
        return NewsDatabase(context)
    }

    @Provides
    fun provideArticleDao(
        database: NewsDatabase
    ): ArticleDAO {
        return database.articlesDao
    }
}
