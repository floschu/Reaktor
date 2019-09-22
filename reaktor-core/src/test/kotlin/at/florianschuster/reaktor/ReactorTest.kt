package at.florianschuster.reaktor

import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.TimeUnit
import org.junit.Test

class ReactorTest {

    @Test
    fun testInitialStateOnlyEmittedOnce() {
        val reactor = TestReactor()
        val testObserver = reactor.state.test()

        assert(testObserver.values().count() == 1)
    }

    @Test
    fun testEachMethodIsInvoked() {
        val reactor = TestReactor()
        val testObserver = reactor.state.test()

        reactor.action.accept(arrayListOf("action"))

        testObserver.let {
            it.assertNoErrors()
            it.values().let { result ->
                assert(result.count() == 2)
                assert(result[0] == listOf("transformedState"))
                assert(
                    result[1] == listOf(
                        "action",
                        "transformedAction",
                        "mutation",
                        "transformedMutation",
                        "transformedState"
                    )
                )
            }
        }
    }

    @Test
    fun testStateReplayCurrentState() {
        val reactor = CounterReactor()
        val testObserver = reactor.state.test() // state: 0

        reactor.action.accept(Unit) // state: 1
        reactor.action.accept(Unit) // state: 2

        testObserver.values().let { result ->
            assert(result.count() == 3)
            assert(result == listOf(0, 1, 2))
        }
    }

    @Test
    fun testCurrentState() {
        val reactor = TestReactor()
        reactor.state
        reactor.action.accept(listOf("action"))

        assert(
            reactor.currentState == listOf(
                "action",
                "transformedAction",
                "mutation",
                "transformedMutation",
                "transformedState"
            )
        )
    }

    @Test
    fun testStateIsCreatedWhenAccessAction() {
        val reactor = TestReactor()
        reactor.action.accept(listOf("action"))

        assert(
            reactor.currentState == listOf(
                "action",
                "transformedAction",
                "mutation",
                "transformedMutation",
                "transformedState"
            )
        )
    }

    @Test
    fun testStreamIgnoresErrorFromMutate() {
        val reactor = CounterReactor()
        val testObserver = reactor.state.test()

        reactor.stateIndexToTriggerError = 2
        reactor.action.accept(Unit)
        reactor.action.accept(Unit)
        reactor.action.accept(Unit)
        reactor.action.accept(Unit)
        reactor.action.accept(Unit)

        testObserver.values().let { result ->
            assert(result.size == 6)
            assert(result == listOf(0, 1, 2, 3, 4, 5))
        }
    }

    @Test
    fun testStreamIgnoresCompletedFromMutate() {
        val reactor = CounterReactor()
        val testObserver = reactor.state.test()

        reactor.stateIndexToTriggerCompleted = 2
        reactor.action.accept(Unit)
        reactor.action.accept(Unit)
        reactor.action.accept(Unit)
        reactor.action.accept(Unit)
        reactor.action.accept(Unit)

        assert(testObserver.values() == listOf(0, 1, 2, 3, 4, 5))
    }

    @Test
    fun testCancel() {
        RxJavaPlugins.reset()

        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        val reactor = StopwatchReactor()

        reactor.action.accept(StopwatchReactor.Action.Start)
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        reactor.action.accept(StopwatchReactor.Action.Stop)

        reactor.action.accept(StopwatchReactor.Action.Start)
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        reactor.action.accept(StopwatchReactor.Action.Stop)

        reactor.action.accept(StopwatchReactor.Action.Start)
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS)
        reactor.action.accept(StopwatchReactor.Action.Stop)

        // this should be ignored
        reactor.action.accept(StopwatchReactor.Action.Start)
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        reactor.action.accept(StopwatchReactor.Action.Stop)

        reactor.action.accept(StopwatchReactor.Action.Start)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        reactor.action.accept(StopwatchReactor.Action.Stop)

        assert(reactor.currentState == 10) // 2+3+4+1

        RxJavaPlugins.reset()
    }

    @Test
    fun testInitialAction() {
        val reactor = TestReactor(initialAction = listOf("initialAction"))
        val testObserver = reactor.state.test()

        testObserver.values().let { result ->
            assert(result.count() == 1)

            assert(
                result[0] == listOf(
                    "initialAction",
                    "transformedAction",
                    "mutation",
                    "transformedMutation",
                    "transformedState"
                )
            )
        }
    }
}

private class TestReactor(
    initialState: List<String> = emptyList(),
    initialAction: List<String>? = null
) : DefaultReactor<List<String>, List<String>, List<String>>(initialState, initialAction) {
    // 1. ["action"] + ["transformedAction"]
    override fun transformAction(action: Observable<List<String>>): Observable<List<String>> {
        return action.map { it + "transformedAction" }
    }

    // 2. ["action", "transformedAction"] + ["mutation"]
    override fun mutate(action: List<String>): Observable<List<String>> {
        return Observable.just(action + "mutation")
    }

    // 3. ["action", "transformedAction", "mutation"] + ["transformedMutation"]
    override fun transformMutation(mutation: Observable<List<String>>): Observable<List<String>> {
        return mutation.map { it + "transformedMutation" }
    }

    // 4. [] + ["action", "transformedAction", "mutation", "transformedMutation"]
    override fun reduce(previousState: List<String>, mutation: List<String>): List<String> {
        return previousState + mutation
    }

    // 5. ["action", "transformedAction", "mutation", "transformedMutation"] + ["transformedState"]
    override fun transformState(state: Observable<List<String>>): Observable<List<String>> {
        return state.map { it + "transformedState" }
    }
}

private class CounterReactor : DefaultReactor<Unit, Unit, Int>(0) {
    var stateIndexToTriggerError: Int? = null
    var stateIndexToTriggerCompleted: Int? = null

    override fun mutate(action: Unit): Observable<Unit> = when (currentState) {
        stateIndexToTriggerError -> {
            val results = arrayOf(Observable.just(action), Observable.error(Error()))
            Observable.concat(results.asIterable())
        }
        stateIndexToTriggerCompleted -> {
            val results = arrayOf(Observable.just(action), Observable.empty())
            Observable.concat(results.asIterable())
        }
        else -> Observable.just(action)
    }

    override fun reduce(previousState: Int, mutation: Unit): Int = previousState + 1
}

private class StopwatchReactor : DefaultReactor<StopwatchReactor.Action, Int, Int>(0) {
    sealed class Action {
        object Start : Action()
        object Stop : Action()
    }

    override fun mutate(action: Action): Observable<out Int> = when (action) {
        is Action.Start -> {
            Observable.interval(1, TimeUnit.SECONDS)
                .takeUntil(this.action.filter { it is Action.Stop })
                .map { 1 }
        }
        is Action.Stop -> Observable.empty()
    }

    override fun reduce(previousState: Int, mutation: Int): Int = previousState + mutation
}
