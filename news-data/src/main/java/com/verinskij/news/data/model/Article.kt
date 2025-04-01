package com.verinskij.news.data.model
data class Article(
    val id: Int = ID_NONE,
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String,
) {
    public companion object {
        /**
         * Специальный ID для обозначения что ID нету
         */
        const val ID_NONE: Int = 0
    }
}

data class Source(
    val id: String?,
    val name: String
)
