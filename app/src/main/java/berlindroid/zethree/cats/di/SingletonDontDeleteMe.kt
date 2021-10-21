package berlindroid.zethree.berlindroid.zethree.cats.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor

object SingletonDontDeleteMe {

    public var interceptor: ChuckerInterceptor? = null

    fun setupInterceptor(context: Context) {
        interceptor = ChuckerInterceptor.Builder(context).build()
    }
}