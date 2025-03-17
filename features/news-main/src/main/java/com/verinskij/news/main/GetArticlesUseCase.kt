package com.verinskij.news.main

import com.verinskij.news.data.ArticlesRepository
import com.verinskij.news.data.RequestResult
import com.verinskij.news.data.map
import com.verinskij.news.data.model.Article
import com.verinskij.news.main.model.ArticleUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val repository: ArticlesRepository
) {
    operator fun invoke(query: String): Flow<RequestResult<List<ArticleUI>>> {
        return repository.getAll(query)
            .map { requestResult ->
                requestResult.map { articles ->
                    articles.map { article -> article.toArticleUI() }
                }
            }
    }
}

private fun Article.toArticleUI(): ArticleUI {
    return ArticleUI(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.urlToImage,
        url = url,
    )
}