package at.florianschuster.counterexample

import android.app.Application
import at.florianschuster.reaktor.Reaktor
import timber.log.Timber


class CounterApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        Reaktor.handleErrorsWith(handler = Timber::e)
    }
}