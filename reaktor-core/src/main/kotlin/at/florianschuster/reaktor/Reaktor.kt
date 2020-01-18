package at.florianschuster.reaktor

import androidx.annotation.RestrictTo
import io.reactivex.Observable

/**
 * Object to handle library initializations.
 */
object Reaktor {
    private var escalateCrashes: Boolean = false
    private var errorHandler: ((Throwable) -> Unit)? = null

    /**
     * Handles error messages, which are swallowed by the state stream by default.
     * Change escalateCrashes to true only when you know what you are doing!
     *
     * @param escalateCrashes Boolean When set to true, certain `Reactor` components crash on errors
     * @param handler (Throwable) -> Unit
     */
    fun attachErrorHandler(
        escalateCrashes: Boolean = false,
        handler: ((Throwable) -> Unit)? = null
    ) {
        this.escalateCrashes = escalateCrashes
        this.errorHandler = handler
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun handleError(throwable: Throwable) {
        when {
            escalateCrashes -> throw throwable
            else -> errorHandler?.invoke(throwable)
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun <S : Any> handleObservableError(throwable: Throwable): Observable<S> {
        return when {
            escalateCrashes -> Observable.error(throwable)
            else -> {
                errorHandler?.invoke(throwable)
                Observable.empty()
            }
        }
    }
}
