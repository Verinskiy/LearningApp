package com.verinskij.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.verinskij.database.model.ArticleDBO

class NewsDatabase internal constructor(private val database: NewsRoomDatabase) {

    val articlesDao: ArticleDAO
        get() = database.articlesDao()
}

@Database(entities = [ArticleDBO::class], version = 1)
internal abstract class NewsRoomDatabase : RoomDatabase() {

    abstract fun articlesDao(): ArticleDAO
}

fun NewsDatabase(applicationContext: Context): NewsDatabase {
    val newsRoomDatabase = Room.databaseBuilder(
        applicationContext.applicationContext,
        NewsRoomDatabase::class.java,
        "news",
    ).build()
    return NewsDatabase(newsRoomDatabase)
}
