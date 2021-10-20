package berlindroid.zethree.cats.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import kotlin.coroutines.coroutineContext

data class CatUiModel(
    val url: String,
    val name: String,
    val energyLevel: Int
)

@Preview
@Composable
@ExperimentalCoilApi
@ExperimentalFoundationApi
fun CatsUi(
    cats: List<CatUiModel> = emptyList()
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(3)
    ) {
        items(cats) { cat ->
            CatUi(cat.url, cat.name, cat.energyLevel)
        }
    }
}

@Preview
@Composable
@ExperimentalCoilApi
fun CatUi(
    imageUrl: String = "https://www.example.com/image.jpg",
    name: String = "Pete",
    energyLevel: Int = 0
) {

    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(
                color = Color.DarkGray,
                shape = RoundedCornerShape(24.dp)
            )
            .wrapContentSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
                    .clickable {
                        showToast(context, name)
                    }

            )
            Text(
                text = "energyLevel: $energyLevel",
                fontSize = 20.sp,
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier
                    .padding(4.dp)

            )
            // TODO: Make text not cut!
            Text(
                text = name,
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        color = Color.Gray,
                        shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
                    )
            )
        }
    }
}

private fun showToast(context: Context, name : String){
    Toast.makeText(context, " name : $name" , Toast.LENGTH_SHORT).show()
}
