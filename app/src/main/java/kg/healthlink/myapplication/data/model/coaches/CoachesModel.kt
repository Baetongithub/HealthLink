package kg.healthlink.myapplication.data.model.coaches

data class CoachesModel(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val whichFitRoom: String = "",
    val urlPhoto: String = ""
) : java.io.Serializable
