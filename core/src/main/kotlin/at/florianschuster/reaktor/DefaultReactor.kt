package at.florianschuster.reaktor

import androidx.annotation.RestrictTo
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

/**
 * Abstract default [Reactor].
 * It handles action and state creation.
 *
 * initialAction is an [Action] that will only be called once when the [Reactor] is initialized.
 */
abstract class DefaultReactor<Action : Any, Mutation : Any, State : Any>(
    final override val initialState: State,
    initialAction: Action? = null
) : Reactor<Action, Mutation, State> {

    override var currentState: State = initialState

    final override val disposables: CompositeDisposable = CompositeDisposable()
    private val _action: ActionRelay<Action> = if (initialAction != null) ActionRelay(initialAction) else ActionRelay()
    final override val action: ActionRelay<Action>
        get() {
            state // creates the state observable, when action is called without subscription to state
            return _action
        }
    final override val state: Observable<out State> by lazy { createStateStream(_action) }
}

/**
 * A simple default Reactor that can be used when there is no need for a Mutation.
 */
abstract class SimpleDefaultReactor<Action : Any, State : Any>(
    initialState: State,
    initialAction: Action? = null
) : DefaultReactor<Action, Action, State>(initialState, initialAction) {

    override fun mutate(action: Action): Observable<Action> = Observable.just(action)
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <Action : Any, Mutation : Any, State : Any> Reactor<Action, Mutation, State>.createStateStream(
    actionBackingFieldValue: ActionRelay<Action>
): Observable<out State> {
    val transformedAction: Observable<out Action> = transformAction(actionBackingFieldValue)

    val mutation: Observable<Mutation> = transformedAction.flatMap {
        mutate(it).onErrorResumeNext { t: Throwable -> Reaktor.handleObservableError(t) }
    }

    val transformedMutation: Observable<out Mutation> = transformMutation(mutation)

    val state: Observable<State> = transformedMutation
        .scan(initialState, ::reduce)
        .onErrorResumeNext { t: Throwable -> Reaktor.handleObservableError(t) }

    val transformedState = transformState(state)
        .doOnNext { currentState = it }
        .replay(1)

    disposables.add(transformedState.connect())

    return transformedState
}
