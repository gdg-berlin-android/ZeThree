package berlindroid.zethree.berlindroid.zethree.dogs

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Url

@Serializable
data class Dog (
    val message: Map<String, List<String>>
)

interface DogsApi {
    @GET
    suspend fun allDogs(@Url url: String = "https://dog.ceo/api/breeds/list/all") : Dog
}