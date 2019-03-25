package at.florianschuster.githubexample

import android.app.Application
import at.florianschuster.reaktor.Reaktor
import timber.log.Timber

class GithubSampleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        Reaktor.handleErrorsWith(handler = Timber::e)
    }
}