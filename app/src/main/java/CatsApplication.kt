package berlindroid.zethree

import android.app.Application
import berlindroid.zethree.berlindroid.zethree.cats.di.SingletonDontDeleteMe
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.ChatDomain

class CatsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SingletonDontDeleteMe.setupInterceptor(this)

        //TODO  do something here

        // Set up Stream chat
        val client = ChatClient.Builder("qharfrf37x32", applicationContext)
            .logLevel(ChatLogLevel.ALL)
            .build()
        ChatDomain.Builder(client, applicationContext).build()
        val user = User(id = "zethree")
        client.connectUser(
            user = user,
            token = client.devToken("zethree")
        ).enqueue()
    }

}