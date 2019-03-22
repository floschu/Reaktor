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
 *
 * initialAction is an [Action] that will only be called once when the [ViewModel] is initialized but not when
 * the corresponding [AppCompatActivity] or [Fragment] is recreated but the [ViewModel] still exists.
 */
abstract class ViewModelReactor<Action : Any, Mutation : Any, State : Any>(
    final override val initialState: State,
    initialAction: Action? = null
) : ViewModel(), Reactor<Action, Mutation, State> {

    override var currentState: State = initialState

    final override val disposables: CompositeDisposable by lazy { CompositeDisposable() }
    final override val action: ActionRelay<Action> = ActionRelay()
    final override val state: Observable<out State> by lazy {
        val stateStream: Observable<out State> = createStateStream()
        initialAction?.let(action::accept)
        stateStream
    }

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
    initialState: State,
    initialAction: Action? = null
) : ViewModelReactor<Action, Action, State>(initialState, initialAction) {

    override fun mutate(action: Action): Observable<Action> = Observable.just(action)

}