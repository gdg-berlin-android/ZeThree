package berlindroid.zethree.cats

import android.util.Log
import androidx.lifecycle.ViewModel
import berlindroid.zethree.cats.CatState.CatsFound
import berlindroid.zethree.cats.CatState.Loading
import berlindroid.zethree.cats.CatState.NoInternet
import berlindroid.zethree.cats.repository.CatApi
import berlindroid.zethree.cats.view.CatUiModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

sealed class CatState {
    object NoInternet : CatState()

    object Loading : CatState()

    data class CatsFound(val cats: List<CatUiModel>) : CatState()
}

@ExperimentalSerializationApi
class CatsViewModel : ViewModel() {
    private val json = Json { ignoreUnknownKeys = true }

    // TODO: Use DI
    private val repository: CatApi by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(
                json.asConverterFactory(
                    MediaType.get("application/json")
                )
            ).build()
            .create(CatApi::class.java)
    }

    val catsFlow: Flow<CatState> = flow {
        emit(Loading)

        try {
            val cats = repository.getCats()
            emit(
                CatsFound(cats.map {
                    CatUiModel(
                        url = it.url,
                        name = it.breeds.firstOrNull()?.name ?: it.id // Todo: be smarter about naming
                    )
                })
            )
        } catch (th: Throwable) {
            Log.e("CATSVM", "No internet?", th)
            emit(NoInternet)
        }
    }
}
