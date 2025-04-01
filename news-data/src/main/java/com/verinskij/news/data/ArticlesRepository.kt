package com.verinskij.news.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.verinskij.database.NewsDatabase
import com.verinskij.news.api.NewsApi
import com.verinskij.news.api.model.ArticleDTO
import com.verinskij.news.common.Logger
import com.verinskij.news.data.model.Article
import com.verinskij.news.data.model.toArticle
import com.verinskij.news.data.model.toArticleDbo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ArticlesRepository @Inject constructor(
    private val database: NewsDatabase,
    private val api: NewsApi,
    private val logger: Logger,
    private val remoteMediator: AllArticlesRemoteMediator.Factory,
    private val articlesPagingSource: AllArticlesPagingSource.Factory
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getAllArticlesPaging(
        query: String,
        config: PagingConfig,
    ): Flow<PagingData<Article>> {
        val pager = Pager(
            config = config,
            remoteMediator = remoteMediator.create(query),
            pagingSourceFactory = {
                database.articlesDao.pagingAll()
            }
        )
        return pager.flow
            .map { pagingData ->
                pagingData.map {
                    it.toArticle()
                }
            }
    }

    fun getAllArticlesPagingTest2(
        query: String,
        config: PagingConfig,
    ): Flow<PagingData<Article>> {
        val pager = Pager(
            config = config,
            pagingSourceFactory = { articlesPagingSource.newInstance(query) }
        )
        return pager.flow
    }

    fun getAll(
        query: String,
        mergeStrategy: MergeStrategy<RequestResult<List<Article>>> = RequestResponseMergeStrategy(),
    ): Flow<RequestResult<List<Article>>> {
        val localCacheArticles = getAllDatabase()
        val remoteArticles = getAllFromServer(query)

        return localCacheArticles.combine(remoteArticles) { dbos, dtos ->
            mergeStrategy.merge(dbos, dtos)
        }
    }

    private fun getAllDatabase(): Flow<RequestResult<List<Article>>> {
        val dbRequest: Flow<RequestResult<List<Article>>> =
            database.articlesDao.getAll()
                .map { articlesDBO -> articlesDBO.map { articleDBO -> articleDBO.toArticle() } }
                .map { articles -> RequestResult.Success(articles) }
                .catch { logger.e(TAG_LOG, "Error getting from database: $it") }
        val start = flowOf<RequestResult<List<Article>>>(RequestResult.InProgress())
        return merge(start, dbRequest)
    }

    private fun getAllFromServer(query: String): Flow<RequestResult<List<Article>>> {
        val remoteArticles: Flow<RequestResult<List<Article>>> = flow {
            emit(api.everything(query = query))
        }.map { result ->
            if (result.isSuccess) {
                val response = result.getOrThrow()
                RequestResult.Success(response.articles)
            } else {
                logger.e(TAG_LOG, "Error from server: ${result.getOrThrow()}")
                RequestResult.Error()
            }
        }.onEach { requestResult ->
            if (requestResult is RequestResult.Success) {
                saveNetResponseToCache(requestResult.data)
            }
        }.map { requestResult ->
            requestResult.map { articleDTOS ->
                articleDTOS.map { it.toArticle() }
            }
        }.onStart { emit(RequestResult.InProgress()) }
        return remoteArticles
    }

    private suspend fun saveNetResponseToCache(data: List<ArticleDTO>) {
        val articlesDBO = data.map { articleDTO -> articleDTO.toArticleDbo() }
        database.articlesDao.insert(articlesDBO)
    }

    companion object {
        private const val TAG_LOG = "Articles repository"
    }
}
