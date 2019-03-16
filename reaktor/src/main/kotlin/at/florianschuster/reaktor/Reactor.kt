package at.florianschuster.reaktor

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


/**
 * A Reactor is an UI-independent layer which manages the state of a view. The foremost role of a
 * [Reactor] is to separate control flow from a view. Every view has its corresponding [Reactor] and
 * delegates all logic to its [Reactor]. A [Reactor] has no dependency to a view, so it can be easily
 * tested.
 */
interface Reactor<Action, Mutation, State> where Action : Any, Mutation : Any, State : Any {
    /**
     * The [CompositeDisposable] that contains the [State] Stream disposable.
     */
    val disposables: CompositeDisposable

    /**
     * The [Action] from the view. Bind user inputs to this relay.
     */
    val action: ActionRelay<Action>

    /**
     * The [State] stream. Use this observable to observe the state changes.
     */
    val state: Observable<out State>

    /**
     * The initial [State].
     */
    val initialState: State

    /**
     * The current [State]. This value is changed just after the state stream emits a new [State].
     */
    var currentState: State

    /**
     * Commits mutation from the [Action]. This is the best place to perform side-effects such as
     * async tasks.
     */
    fun mutate(action: Action): Observable<out Mutation> = Observable.empty()

    /**
     * Generates a new state with the previous [State] and the [Action]. It should be purely functional
     * so it should not perform any side-effects here. This method is called every time when
     * the [Mutation] is committed.
     */
    fun reduce(state: State, mutation: Mutation): State = state

    /**
     * Transforms the [Action]. Use this function to combine with other observables. This method is
     * called once before the state stream is created.
     */
    fun transformAction(action: Observable<Action>): Observable<out Action> = action

    /**
     * Transforms the [Mutation] stream. Implement this method to transform or combine with other
     * observables. This method is called once before the state stream is created.
     */
    fun transformMutation(mutation: Observable<Mutation>): Observable<out Mutation> = mutation

    /**
     * Transforms the [State] stream. Use this function to perform side-effects such as logging. This
     * method is called once after the state stream is created.
     */
    fun transformState(state: Observable<State>): Observable<out State> = state

    /**
     * Creates the [State] stream by transforming the [ActionRelay] to a [State] observable.
     */
    fun createStateStream(): Observable<out State> {
        val transformedAction: Observable<out Action> = transformAction(action)

        val mutation: Observable<Mutation> = transformedAction.flatMap {
            mutate(it).onErrorResumeNext { t: Throwable -> Reaktor.handleObservableError(t) }
        }

        val transformedMutation: Observable<out Mutation> = transformMutation(mutation)

        val state: Observable<State> = transformedMutation
            .scan(initialState) { state, mutate -> reduce(state, mutate) }
            .onErrorResumeNext { t: Throwable -> Reaktor.handleObservableError(t) }
            .startWith(initialState)

        val transformedState = transformState(state)
            .doOnNext { currentState = it }
            .replay(1)

        disposables.add(transformedState.connect())

        return transformedState
    }
}
