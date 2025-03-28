package com.verinskij.news.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verinskij.news.data.RequestResult
import com.verinskij.news.main.model.ArticleUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
internal class NewsMainViewModel @Inject constructor(
    private val getArticlesUseCase: Provider<GetArticlesUseCase>
) : ViewModel() {

    val state: StateFlow<State> = getArticlesUseCase.get().invoke("android")
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)

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