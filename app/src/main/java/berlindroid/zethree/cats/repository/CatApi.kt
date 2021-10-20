package berlindroid.zethree.cats.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET

@Serializable
data class CatModel(
    @SerialName("breeds") val breeds: List<CatBreedModel>,
    @SerialName("id") val id: String,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("url") val url: String,
)

@Serializable
data class CatBreedModel(
    @SerialName("id") val id: String, /* = "esho",*/
    @SerialName("name") val name: String, /* = "Exotic Shorthair",*/
    @SerialName("temperament") val temperament: String, /* = "Affectionate, Sweet, Loyal, Quiet, Peaceful",*/
    @SerialName("origin") val origin: String, /* = "United States",*/
    @SerialName("country_codes") val countryCodes: String, /* = "US",*/
    @SerialName("country_code") val countryCode: String, /* = "US",*/
    @SerialName("description") val description: String, /* = "The Exotic Shorthair is a gentle friendly cat that …",*/
    @SerialName("life_span") val lifeSpan: String, /* = "12 - 15",*/
    @SerialName("adaptability") val adaptability: Int, /* = 5,*/
    @SerialName("affection_level") val affectionLevel: Int, /* = 5,*/
    @SerialName("child_friendly") val childFriendly: Int, /* = 3,*/
    @SerialName("dog_friendly") val dogFriendly: Int, /* = 3,*/
    @SerialName("energy_level") val energyLevel: Int, /* = 3,*/
    @SerialName("grooming") val grooming: Int, /* = 2,*/
    @SerialName("health_issues") val healthIssues: Int, /* = 3,*/
    @SerialName("intelligence") val intelligence: Int, /* = 3,*/
    @SerialName("shedding_level") val sheddingLevel: Int, /* = 2,*/
    @SerialName("social_needs") val socialNeeds: Int, /* = 4,*/
    @SerialName("stranger_friendly") val strangerFriendly: Int, /* = 2,*/
    @SerialName("vocalisation") val vocalisation: Int, /* = 1,*/
    @SerialName("experimental") val experimental: Int, /* = 0,*/
    @SerialName("hairless") val hairless: Int, /* = 0,*/
    @SerialName("natural") val natural: Int, /* = 0,*/
    @SerialName("rare") val rare: Int, /* = 0,*/
    @SerialName("rex") val rex: Int, /* = 0,*/
    @SerialName("suppressed_tail") val suppressedTail: Int, /* = 0,*/
    @SerialName("short_legs") val shortLegs: Int, /* = 0,*/
    @SerialName("wikipedia_url") val wikipediaUrl: String, /* = "https://en.wikipedia.org/wiki/Exotic_Shorthair",*/
    @SerialName("hypoallergenic") val hypoallergenic: Int, /* = 0,*/
)

interface CatApi {
    // TODO: make url using url parameters
    @GET("images/search?limit=15&page=10&order=Desc")
    suspend fun getCats(): List<CatModel>
}

private val json = Json { ignoreUnknownKeys = true }

// TODO: Use DI
@ExperimentalSerializationApi
fun provideCatApi(): CatApi = Retrofit
    .Builder()
    .baseUrl("https://api.thecatapi.com/v1/")
    .client(
        OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()
    )
    .addConverterFactory(
        json.asConverterFactory(
            MediaType.get("application/json")
        )
    ).build()
    .create(CatApi::class.java)
