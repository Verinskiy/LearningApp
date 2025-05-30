package com.verinskij.news.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.verinskij.news.api.NewsApi
import com.verinskij.news.data.model.Article
import com.verinskij.news.data.model.toArticle
import javax.inject.Inject
import javax.inject.Provider

class AllArticlesPagingSource(
    private val query: String,
    private val newsApi: NewsApi,
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        val result = newsApi.everything(query, page = page, pageSize = params.loadSize)
            .map { response -> response.articles.map { it.toArticle(id = counter++) } }
        when {
            result.isSuccess -> {
                val articles: List<Article> = result.getOrThrow()
                val prev: Int? = params.key?.let { it - 1 }?.takeIf { it > 0 }
                val next: Int? = if (articles.isEmpty()) null else page + 1
                return LoadResult.Page(articles, prevKey = prev, nextKey = next)
            }

            result.isFailure -> return LoadResult.Error(result.exceptionOrNull() ?: UnknownError())
            else -> error("ImpossibleBranch")
        }
    }

    class Factory @Inject constructor(private val newsApi: Provider<NewsApi>) {
        fun newInstance(query: String): PagingSource<Int, Article> {
            return AllArticlesPagingSource(query, newsApi.get())
        }
    }

    private companion object {
        var counter: Int = 1
    }
}
