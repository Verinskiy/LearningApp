package com.verinskij.news.main

import android.inputmethodservice.Keyboard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.verinskij.news.data.model.Article
import com.verinskij.news.main.model.ArticleUI

@Composable
fun NewsMainScreen() {
    NewsMainScreen(viewModel = viewModel())
}

@Composable
internal fun NewsMainScreen(viewModel: NewsMainViewModel) {
    val state by viewModel.state.collectAsState()
    when (val currentState = state) {
        is State.Success -> Articles(currentState.articles)
        is State.Error -> ArticlesWithError(currentState.articles)
        is State.Loading -> ArticleDuringUpdate(currentState.articles)
        State.None -> NewsEmpty()
    }
}

@Composable
fun ArticlesWithError(articles: List<ArticleUI>?) {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.error)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error during update", color = Color.Cyan)
        }
        if (articles != null) {
            Articles(articles)
        }
    }
}

@Composable
fun ArticleDuringUpdate(articles: List<ArticleUI>?) {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        if (articles != null) {
            Articles(articles)
        }
    }
}

@Composable
fun NewsEmpty() {
    Box(contentAlignment = Alignment.Center) {
        Text("!!!!!!!!!!!!!!!!!! No news")
    }
}

@Composable
private fun Articles(articles: List<ArticleUI>) {
    LazyColumn {
        items(articles) { article ->
            key(article.id) {
                Article(article)
            }
        }
    }
}

@Composable
internal fun Article(article: ArticleUI) {
    Row {
        Column {
            article.imageUrl?.let { image ->
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .heightIn(150.dp)
                        .widthIn(max = 150.dp)
                )
            }
            Text(text = article.title, style = MaterialTheme.typography.headlineMedium, maxLines = 1)
            Text(text = article.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
        }
    }
}