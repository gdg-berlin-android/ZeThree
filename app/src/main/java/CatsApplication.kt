package berlindroid.zethree

import android.app.Application
import berlindroid.zethree.cats.di.catsModule
import org.koin.core.context.startKoin

class CatsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // use Koin logger
            printLogger()
            // declare modules
            modules(catsModule)
        }

    }

}