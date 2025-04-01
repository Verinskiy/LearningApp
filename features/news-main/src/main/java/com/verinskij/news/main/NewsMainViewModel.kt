package com.verinskij.news.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.verinskij.news.data.ArticlesRepository
import com.verinskij.news.data.RequestResult
import com.verinskij.news.main.model.ArticleUI
import com.verinskij.news.main.model.toArticleUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
internal class NewsMainViewModel @Inject constructor(
    private val getArticlesUseCase: Provider<GetArticlesUseCase>,
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    private val _query: MutableStateFlow<String> = MutableStateFlow("q")

    val query: StateFlow<String>
        get() = _query.asStateFlow()

    private val pagingConfig = PagingConfig(
        initialLoadSize = 10,
        pageSize = 10,
        maxSize = 100,
        enablePlaceholders = false
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val statePaging: StateFlow<PagingData<ArticleUI>> =
        query.map { query ->
            articlesRepository.getAllArticlesPagingTest2(
                query = query,
                config = pagingConfig
            )
        }.flatMapConcat { pagingDataFlow ->
            pagingDataFlow.map { pagingData ->
                pagingData.map {
                    it.toArticleUI()
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    /*val state: StateFlow<State> = getArticlesUseCase.get().invoke("")
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)*/

    private fun RequestResult<List<ArticleUI>>.toState(): State {
        return when (this) {
            is RequestResult.Error -> State.Error(data)
            is RequestResult.InProgress -> State.Loading(data)
            is RequestResult.Success -> State.Success(data)
        }
    }
}

sealed class State(open val articles: List<ArticleUI>?) {

    object None : State(null)
    class Loading(articles: List<ArticleUI>? = null) : State(articles)
    class Error(articles: List<ArticleUI>? = null, error: kotlin.Error? = null) : State(articles)
    class Success(override val articles: List<ArticleUI>) : State(articles)
}
