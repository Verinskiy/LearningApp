package com.verinskij.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.verinskij.database.model.ArticleDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDAO {

    @Query("SELECT * FROM articles")
    fun getAll(): Flow<List<ArticleDBO>>

    @Insert
    suspend fun insert(articles: List<ArticleDBO>)

    @Delete
    suspend fun remove(articles: List<ArticleDBO>)

    @Query("DELETE FROM articles")
    suspend fun clean()
}
