package berlindroid.zethree

import android.app.Application
import berlindroid.zethree.berlindroid.zethree.cats.di.SingletonDontDeleteMe

class CatsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SingletonDontDeleteMe.setupInterceptor(this)

        //TODO  do something here
    }

}