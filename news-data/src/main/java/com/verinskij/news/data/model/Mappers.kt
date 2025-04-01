package com.verinskij.news.data.model

import com.verinskij.database.model.ArticleDBO
import com.verinskij.database.model.SourceDBO
import com.verinskij.news.api.model.ArticleDTO
import com.verinskij.news.api.model.SourceDTO

internal fun ArticleDBO.toArticle(): Article {
    return Article(
        id = id,
        source = this.source.toSource(),
        author = this.author,
        title = this.title,
        description = this.description,
        url = this.url,
        urlToImage = this.urlToImage,
        publishedAt = this.publishedAt,
        content = this.content
    )
}

internal fun ArticleDTO.toArticleDbo(): ArticleDBO {
    return ArticleDBO(
        source = this.source.toSourceDBO(),
        author = this.author,
        title = this.title,
        description = this.description,
        url = this.url,
        urlToImage = this.urlToImage,
        publishedAt = this.publishedAt,
        content = this.content
    )
}

internal fun SourceDBO.toSource(): Source {
    return Source(
        id = id,
        name = name
    )
}

internal fun SourceDTO.toSourceDBO(): SourceDBO {
    return SourceDBO(
        id = this.id,
        name = name
    )
}

internal fun SourceDTO.toSource(): Source {
    return Source(
        id = id,
        name = name
    )
}

internal fun ArticleDTO.toArticle(id: Int = 0): Article {
    return Article(
        id = id,
        source = this.source.toSource(),
        author = this.author,
        title = this.title,
        description = this.description,
        url = this.url,
        urlToImage = this.urlToImage,
        publishedAt = this.publishedAt,
        content = this.content
    )
}
