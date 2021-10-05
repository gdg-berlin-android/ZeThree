package berlindroid.zethree.cats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import berlindroid.zethree.R.string
import berlindroid.zethree.cats.CatState.CatsFound
import berlindroid.zethree.cats.CatState.Loading
import berlindroid.zethree.cats.CatState.NoInternet
import berlindroid.zethree.cats.CatsViewModel
import berlindroid.zethree.cats.view.CatsUi
import berlindroid.zethree.cats.ui.theme.ZeThreeTheme
import coil.annotation.ExperimentalCoilApi
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalSerializationApi
class CatsActivity : ComponentActivity() {

    private val viewModel: CatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZeThreeTheme {
                val catState by viewModel.catsFlow.collectAsState(NoInternet)

                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(stringResource(string.app_name)) })
                    },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when (catState) {
                        is CatsFound -> {
                            val cats = (catState as CatsFound).cats
                            if (cats.isNotEmpty()) {
                                CatsUi(cats)
                            } else {
                                NoCatsUi()
                            }
                        }
                        Loading -> LoadingUi()
                        NoInternet -> NoInternetUi()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NoCatsUi() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Text(
            text = "No cats found. 😿",
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun LoadingUi() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(100.dp)
                .padding(4.dp),
            strokeWidth = 8.dp,
        )
        Text(
            text = "Loading",
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun NoInternetUi() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Text(
            text = "Loading",
            modifier = Modifier
                .background(
                    color = Color.Green,
                    shape = RoundedCornerShape(16.dp)
                ),
            textAlign = TextAlign.Center
        )
    }
}
