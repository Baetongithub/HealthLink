package kg.healthlink.myapplication.data.model.news

import java.io.Serializable

data class Article(
    val author: Any,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String? = null,
    val urlToImage: String
) : Serializable