package berlindroid.zethree.dogs


import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import berlindroid.zethree.R
import berlindroid.zethree.berlindroid.zethree.dogs.DogsApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create


class DogsActivity : ComponentActivity() {
    private var mediaPlayer : MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeTheDogBark()
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://google.com")
            .client(
                OkHttpClient().newBuilder()
                    //.addInterceptor(SingletonDontDeleteMe.interceptor)
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level =
                            HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            ).build()
        val api = retrofit.create<DogsApi>()

        lifecycleScope.launch {
            val dog = api.allDogs()
            setContent {
                Column {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Dog are the best"
                    )
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = dog.message.keys.joinToString()
                    )
                    OutlinedButton(onClick = {
                        if(mediaPlayer!!.isPlaying){
                            mediaPlayer!!.stop()
                        }
                    }) {
                        Text("I'm an Outlined Button")
                    }
                }
            }
        }
    }

    private fun makeTheDogBark() {
        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.dogbark)
        }
        mediaPlayer!!.start()
    }
}