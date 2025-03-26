package com.verinskij.news.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.verinskij.database.ArticleDAO
import com.verinskij.database.model.ArticleDBO
import com.verinskij.news.api.NewsApi
import com.verinskij.news.data.model.toArticleDbo
import javax.inject.Inject
import javax.inject.Provider

@OptIn(ExperimentalPagingApi::class)
class AllArticlesRemoteMediator internal constructor(
    private val query: String,
    private val articleDao: ArticleDAO,
    private val networkService: NewsApi,
) : RemoteMediator<Int, ArticleDBO>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleDBO>
    ): MediatorResult {
        val pageSize: Int = state.config.pageSize.coerceAtMost(NewsApi.MAX_PAGE_SIZE)

        val page: Int = getPage(loadType, state) ?: return MediatorResult.Success(
            endOfPaginationReached = false
        )

        val networkResult = networkService.everything(query, page = page, pageSize = pageSize)
        if (networkResult.isSuccess) {
            val totalResults = networkResult.getOrThrow().totalResults
            val articlesDbo = networkResult.getOrThrow().articles.map { it.toArticleDbo() }
            if (loadType == LoadType.REFRESH) {
                articleDao.cleanAndInsert(articlesDbo)
            } else {
                articleDao.insert(articlesDbo)
            }

            return MediatorResult.Success(
                endOfPaginationReached = articlesDbo.size < pageSize
            )
        }
        return MediatorResult.Error(networkResult.exceptionOrNull() ?: UnknownError())
    }

    private fun getPage(loadType: LoadType, state: PagingState<Int, ArticleDBO>): Int? =
        when (loadType) {
            LoadType.REFRESH ->
                state.anchorPosition?.let { state.closestPageToPosition(it) }?.prevKey ?: 1

            LoadType.PREPEND -> null

            LoadType.APPEND -> {
                val lastPage = state.pages.lastOrNull()
                if (lastPage == null) {
                    1
                } else {
                    state.pages.size + 1
                }
            }
        }

    class Factory @Inject constructor(
        private val articleDao: Provider<ArticleDAO>,
        private val networkService: Provider<NewsApi>,
    ) {
        fun create(query: String): AllArticlesRemoteMediator {
            return AllArticlesRemoteMediator(
                query = query,
                articleDao = articleDao.get(),
                networkService = networkService.get()
            )
        }
    }
}