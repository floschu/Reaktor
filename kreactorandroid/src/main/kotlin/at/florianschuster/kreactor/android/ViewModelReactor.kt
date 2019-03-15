package at.florianschuster.kreactor.android

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.florianschuster.kreactor.ActionRelay
import at.florianschuster.kreactor.Reactor
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


/**
 * Abstract Reactor implementing ViewModel.
 * It handles action and state creation and clearing of the state observable.
 */
abstract class ViewModelReactor<Action : Any, Mutation : Any, State : Any>(
    final override val initialState: State,
    override var currentState: State = initialState
) : ViewModel(), Reactor<Action, Mutation, State> {
    final override val disposables = CompositeDisposable()
    final override val action: ActionRelay<Action> = ActionRelay()
    final override val state: Observable<out State> by lazy { createStateStream() }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}


/**
 * A simple ViewModel Reactor that can be used when there is no need for a Mutation.
 */
abstract class SimpleViewModelReactor<Action : Any, State : Any>(
    initialState: State
) : ViewModelReactor<Action, Action, State>(initialState) {
    override fun mutate(action: Action): Observable<Action> = Observable.just(action)
}


/**
 * Lazily gets or creates the ViewModel Reactor for a FragmentActivity scope.
 */
inline fun <reified R> FragmentActivity.viewModelReactor(): Lazy<R> where R : Reactor<*, *, *>, R : ViewModel =
    lazy { ViewModelProviders.of(this).get(R::class.java) }

/**
 * Lazily gets or creates the ViewModel Reactor for a FragmentActivity scope with a provided initialization lambda.
 */
inline fun <reified R> FragmentActivity.viewModelReactor(crossinline factory: () -> R): Lazy<R> where R : Reactor<*, *, *>, R : ViewModel =
    lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
        }).get(R::class.java)
    }


/**
 * Lazily gets or creates the ViewModel Reactor for a Fragment scope.
 */
inline fun <reified R> Fragment.viewModelReactor(): Lazy<R> where R : Reactor<*, *, *>, R : ViewModel =
    lazy { ViewModelProviders.of(this).get(R::class.java) }

/**
 * Lazily gets or creates the ViewModel Reactor for a Fragment scope with a provided initialization lambda.
 */
inline fun <reified R> Fragment.viewModelReactor(crossinline factory: () -> R): Lazy<R> where R : Reactor<*, *, *>, R : ViewModel =
    lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
        }).get(R::class.java)
    }