package berlindroid.zethree.cats.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

data class CatUiModel(
    val url: String,
    val name: String
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
            CatUi(cat.url, cat.name)
        }
    }
}

@Preview
@Composable
@ExperimentalCoilApi
fun CatUi(
    imageUrl: String = "https://www.example.com/image.jpg",
    name: String = "Pete"
) {
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
