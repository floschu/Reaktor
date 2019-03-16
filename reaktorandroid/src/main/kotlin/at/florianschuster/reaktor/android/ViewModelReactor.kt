package at.florianschuster.reaktor.android

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import at.florianschuster.reaktor.ActionRelay
import at.florianschuster.reaktor.Reactor
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
    final override val disposables: CompositeDisposable by lazy { CompositeDisposable() }
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