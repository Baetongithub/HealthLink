package kg.healthlink.myapplication.data.model.reviews

data class ReviewsModel(
    val id: Int = 0,
    val reviewOwner: String = "",
    val review: String = "",
    val rating: Float = 0f
):java.io.Serializable
