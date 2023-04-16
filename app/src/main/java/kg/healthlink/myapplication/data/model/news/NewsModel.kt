package kg.healthlink.myapplication.data.model.news

data class NewsModel(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)