package berlindroid.zethree.view

import app.cash.turbine.test
import berlindroid.zethree.cats.CatState
import berlindroid.zethree.cats.CatsViewModel
import berlindroid.zethree.cats.repository.CatApi
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
class CatsViewModelTest {

    @MockK
    private lateinit var catApi: CatApi

    private lateinit var catsViewModel: CatsViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        catsViewModel = CatsViewModel(catApi)
    }

    @Test
    fun catsFlow_success() = runBlocking {
        //given
        coEvery { catApi.getCats() } returns (listOf())

        //when
        val result = catsViewModel.catsFlow

        //then
        result.test {
            assertEquals(CatState.Loading, awaitItem())
            assertEquals(CatState.CatsFound(listOf()), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun catsFlow_error() = runBlocking {
        //given
        coEvery { catApi.getCats() } throws (Exception())

        //when
        var result: Flow<CatState> = flow { }

        try {
            result = catsViewModel.catsFlow
        } catch (e: Exception) {
            //then
            result.test {
                assertEquals(CatState.Loading, awaitItem())
                assertEquals(CatState.NoInternet, awaitItem())
                awaitComplete()
            }
        }
    }
}