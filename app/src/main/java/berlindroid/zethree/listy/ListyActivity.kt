package berlindroid.zethree.berlindroid.zethree.listy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import berlindroid.zethree.cats.ui.theme.ZeThreeTheme

class ListyActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ZeThreeTheme {
                Scaffold {
                    val myListItems = remember {
                        listOf("Cats", "Dogs", "Penguins", "Pingolins", "Crows", "Sparrows", "Mice", "Rats",
                            "Snakes", "Parrots", "Koalas", "Pandas", "Red Pandas")
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(myListItems) { animal ->
                            Text(
                                text = animal,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }


        }
    }
}