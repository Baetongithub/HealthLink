package kg.healthlink.myapplication.data.model.reviews

data class ReviewsModel(
    val id: Int = 0,
    val rating: Double = 0.0,
    val review: String = "",
    val reviewOwner: String = ""
) : java.io.Serializable
