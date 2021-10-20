package berlindroid.zethree.cats

import android.util.Log
import androidx.lifecycle.ViewModel
import berlindroid.zethree.cats.CatState.CatsFound
import berlindroid.zethree.cats.CatState.Loading
import berlindroid.zethree.cats.CatState.NoInternet
import berlindroid.zethree.cats.repository.CatApi
import berlindroid.zethree.cats.repository.provideCatApi
import berlindroid.zethree.cats.view.CatUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi

sealed class CatState {
    object NoInternet : CatState()

    object Loading : CatState()

    data class CatsFound(val cats: List<CatUiModel>) : CatState()
}

@ExperimentalSerializationApi
class CatsViewModel(private val catApi: CatApi = provideCatApi()) : ViewModel() {

    val catsFlow: Flow<CatState> = flow {
        emit(Loading)

        try {
            val cats = catApi.getCats()
            emit(
                CatsFound(cats.map {
                    CatUiModel(
                        url = it.url,
                        name = it.breeds.firstOrNull()?.name ?: it.id, // Todo: be smarter about naming
                        energyLevel = it.breeds.firstOrNull()?.energyLevel ?: 0
                    )
                })
            )
        } catch (th: Throwable) {
            Log.e("CATSVM", "No internet?", th)
            emit(NoInternet)
        }
    }
}
