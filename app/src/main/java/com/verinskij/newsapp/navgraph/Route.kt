package com.verinskij.newsapp.navgraph

sealed class Route(
    val route: String
) {
    data object HomeScreen : Route(route = "homeScreen")
    data object DetailScreen : Route(route = "detailScreen")
    data object SearchScreen : Route(route = "searchScreen")
    data object BookmarkScreen : Route(route = "bookmarkScreen")
}