package com.karate.kime.data.youtube


data class YouTubeResponse(
    val items: List<YouTubeItem> = emptyList()
)

data class YouTubeItem(
    val id: String,
    val snippet: Snippet
)

data class Snippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails
)

data class Thumbnails(
    val default: Thumbnail? = null,
    val medium: Thumbnail? = null,
    val high: Thumbnail? = null
)

data class Thumbnail(
    val url: String
)
