package at.florianschuster.reaktor.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.toLiveData
import at.florianschuster.reaktor.Reactor
import io.reactivex.BackpressureStrategy

/**
 * Converts the state stream to a [LiveData] object that can be bound to a view's lifecycle.
 */
fun <State : Any> Reactor<*, *, State>.liveDataState(
    backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST
): LiveData<out State> {
    return state.toFlowable(backpressureStrategy).toLiveData()
}