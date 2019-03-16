package at.florianschuster.reaktor

import io.reactivex.Observable
import io.reactivex.disposables.Disposable


/**
 * Consumes a [Reactor] Action with the corresponding [Reactor].
 */
fun <Action : Any> Observable<Action>.consume(with: Reactor<in Action, *, *>): Disposable =
    subscribe(with.action::accept, Reaktor::handleError)


/**
 * Maps changes from a [State] Observable and only emits those that are distinct from their immediate predecessors.
 */
fun <State : Any, SubState : Any> Observable<State>.changesFrom(mapper: (State) -> SubState): Observable<out SubState> {
    return map(mapper::invoke)
        .distinctUntilChanged()
}