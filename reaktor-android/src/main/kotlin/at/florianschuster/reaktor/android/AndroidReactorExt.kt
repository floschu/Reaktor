package at.florianschuster.reaktor.android

import at.florianschuster.reaktor.Reaktor
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * Binds an observable to the UI. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Observable<State>.bind(): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .doOnError(Reaktor::handleError)
        .subscribe()
}

/**
 * Binds an observable to a UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Observable<State>.bind(to: Consumer<in State>): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Consumer(Reaktor::handleError))
}

/**
 * Binds an observable to a UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Observable<State>.bind(to: (State) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Reaktor::handleError)
}
