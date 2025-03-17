package com.verinskij.news.data.model

import java.util.Date

data class Article(
    val id: Long = ID_NONE,
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String,
)
{
    public companion object {
        /**
         * Специальный ID для обозначения что ID нету
         */
        public const val ID_NONE: Long = 0L
    }
}

data class Source(
    val id: String?,
    val name: String
)
