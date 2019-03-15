package at.florianschuster.kreactor

import io.reactivex.Observable


/**
 * Object to handle Library initializations.
 */
object KReactor {
    private var crashInDebug: Boolean = true
    private var errorHandler: ((Throwable) -> Unit)? = null

    private val shouldCrash: Boolean
        get() = KReactor.crashInDebug && BuildConfig.DEBUG

    /**
     * Handles error messages, which are swallowed by the state stream by default.
     *
     * @param crashInDebug Boolean
     * @param handler (Throwable) -> Unit
     */
    fun handleErrorsWith(crashInDebug: Boolean = true, handler: ((Throwable) -> Unit)? = null) {
        this.crashInDebug = crashInDebug
        this.errorHandler = handler
    }

    fun handleError(throwable: Throwable) {
        when {
            shouldCrash -> throw throwable
            else -> errorHandler?.invoke(throwable)
        }
    }

    internal fun <S : Any> handleObservableError(throwable: Throwable): Observable<S> {
        return when {
            shouldCrash -> Observable.error(throwable)
            else -> {
                errorHandler?.invoke(throwable)
                Observable.empty()
            }
        }
    }
}