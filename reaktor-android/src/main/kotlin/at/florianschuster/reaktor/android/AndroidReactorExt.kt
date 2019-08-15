package at.florianschuster.reaktor.android

import at.florianschuster.reaktor.Reaktor
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * Binds an [Observable] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Observable<State>.bind(): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe({}, Reaktor::handleError)
}

/**
 * Binds an [Observable] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Observable<State>.bind(to: Consumer<in State>): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Consumer(Reaktor::handleError))
}

/**
 * Binds an [Observable] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Observable<State>.bind(to: (State) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Reaktor::handleError)
}

/**
 * Binds a [Flowable] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Flowable<State>.bind(): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe({}, Reaktor::handleError)
}

/**
 * Binds a [Flowable] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Flowable<State>.bind(to: Consumer<in State>): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Consumer(Reaktor::handleError))
}

/**
 * Binds a [Flowable] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Flowable<State>.bind(to: (State) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Reaktor::handleError)
}

/**
 * Binds a [Single] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Single<State>.bind(): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe({}, Reaktor::handleError)
}

/**
 * Binds a [Single] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Single<State>.bind(to: Consumer<in State>): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Consumer(Reaktor::handleError))
}

/**
 * Binds a [Single] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Single<State>.bind(to: (State) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Reaktor::handleError)
}

/**
 * Binds a [Completable] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun Completable.bind(): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe({}, Reaktor::handleError)
}

/**
 * Binds a [Completable] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun Completable.bind(to: Action): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Consumer(Reaktor::handleError))
}

/**
 * Binds a [Completable] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun Completable.bind(to: () -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Reaktor::handleError)
}

/**
 * Binds a [Maybe] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Maybe<State>.bind(): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe({}, Reaktor::handleError)
}

/**
 * Binds a [Maybe] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Maybe<State>.bind(to: Consumer<in State>): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Consumer(Reaktor::handleError))
}

/**
 * Binds a [Maybe] to an UI target. Also logs errors in [Reaktor].
 */
@CheckReturnValue
fun <State : Any> Maybe<State>.bind(to: (State) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(to, Reaktor::handleError)
}
