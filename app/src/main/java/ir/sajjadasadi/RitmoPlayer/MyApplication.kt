package ir.sajjadasadi.RitmoPlayer

import android.app.Application
import com.airbnb.lottie.BuildConfig
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}