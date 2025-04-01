
package com.verinskij.news.main
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.verinskij.news.main.model.ArticleUI

@Composable
fun NewsMainScreen(modifier: Modifier = Modifier) {
    NewsMainScreen(modifier = modifier, viewModel = viewModel())
}

@Composable
internal fun NewsMainScreen(modifier: Modifier, viewModel: NewsMainViewModel) {
    val articlesItems: LazyPagingItems<ArticleUI> = viewModel.statePaging.collectAsLazyPagingItems()
    NewsMainContent(articlesItems, modifier)
}

@Composable
private fun NewsMainContent(
    articlesItems: LazyPagingItems<ArticleUI>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        when (articlesItems.loadState.refresh) {
            is LoadState.Error -> ErrorMessage()
            is LoadState.Loading -> ProgressIndicator()
            is LoadState.NotLoading -> ArticleList(articlesItems)
        }
    }
}

@Composable
internal fun ErrorMessage() {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Color.Yellow)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error during update", color = Color.Blue)
        }
    }
}

@Composable
internal fun ProgressIndicator() {
    CircularProgressIndicator(Modifier.padding(8.dp))
}
