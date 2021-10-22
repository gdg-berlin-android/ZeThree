package berlindroid.zethree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {

            val shouldChet by remember { mutableStateOf(shouldShowChet()) }

            ChatTheme {
                if(shouldChet){
                        ChetFaceScreen()
                }else {
                    MessagesScreen(
                        channelId = "messaging:zethree-app-channel-0",
                        onBackPressed = ::finish
                    )
                }
            }
        }
    }

    private fun shouldShowChet(): Boolean{
        return (1..2).random() == 1
    }

}



@Composable
fun ChetFaceScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        //CHET IMGE
        Image(painter = painterResource(id = R.drawable.chet), contentDescription = "Practice practice practice. Chet is king #ChetBoi4Lyf")
    }
}