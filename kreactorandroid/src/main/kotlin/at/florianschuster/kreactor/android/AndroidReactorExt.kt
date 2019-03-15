package at.florianschuster.kreactor.android

import at.florianschuster.kreactor.KReactor
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer


/**
 * Binds an observable to the UI. Also logs errors in [KReactor].
 */
fun <State : Any> Observable<State>.bind(): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .doOnError(KReactor::handleError)
        .subscribe()
}

/**
 * Binds an observable to a UI target. Also logs errors in [KReactor].
 */
fun <State : Any> Observable<State>.bind(to: Consumer<in State>): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Consumer(KReactor::handleError))
}

/**
 * Binds an observable to a UI target. Also logs errors in [KReactor].
 */
fun <State : Any> Observable<State>.bind(to: (State) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, KReactor::handleError)
}