package at.florianschuster.savedstateexample

import android.app.Application
import at.florianschuster.reaktor.Reaktor
import timber.log.Timber

class SavedStateApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        Reaktor.attachErrorHandler(escalateCrashes = BuildConfig.DEBUG, handler = Timber::e)
    }
}
