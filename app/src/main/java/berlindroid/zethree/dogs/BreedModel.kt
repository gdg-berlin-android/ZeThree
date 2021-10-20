package berlindroid.zethree.berlindroid.zethree.dogs

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class BreedResponse(
    val message: JsonObject,
    val status: String
)
