package com.verinskij.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.verinskij.database.model.ArticleDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDAO {

    @Query("SELECT * FROM articles")
    fun getAll(): Flow<List<ArticleDBO>>

    @Query("SELECT * FROM articles")
    fun pagingAll(): PagingSource<Int, ArticleDBO>

    @Insert
    suspend fun insert(articles: List<ArticleDBO>)

    @Delete
    suspend fun remove(articles: List<ArticleDBO>)

    @Query("DELETE FROM articles")
    suspend fun clean()

    @Transaction
    suspend fun cleanAndInsert(articles: List<ArticleDBO>) {
        // Anything inside this method runs in a single transaction.
        clean()
        insert(articles)
    }
}
