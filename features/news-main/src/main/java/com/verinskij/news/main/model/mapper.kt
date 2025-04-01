package com.verinskij.news.main.model

import com.verinskij.news.data.model.Article

internal fun Article.toArticleUI(): ArticleUI {
    return ArticleUI(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.urlToImage,
        url = url,
    )
}