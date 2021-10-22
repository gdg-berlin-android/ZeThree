package berlindroid.zethree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            ChatTheme {
                MessagesScreen(
                    channelId = "messaging:zethree-app-channel-0",
                    onBackPressed = ::finish
                )
            }
        }
    }
}