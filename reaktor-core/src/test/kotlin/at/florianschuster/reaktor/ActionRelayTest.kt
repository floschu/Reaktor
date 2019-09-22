package at.florianschuster.reaktor

import org.junit.Test

class ActionRelayTest {

    @Test
    fun testAccept() {
        val relay = ActionRelay<Int>()
        val testObserver = relay.test()

        relay.accept(0)
        relay.accept(1)
        relay.accept(2)

        testObserver.values().let { result ->
            assert(result.size == 3)
            assert(result == listOf(0, 1, 2))
        }
    }

    @Test
    fun testObservers() {
        val relay = ActionRelay<Int>()
        assert(relay.observerCount == 0)

        relay.subscribe()
        assert(relay.observerCount == 1)

        relay.subscribe()
        assert(relay.observerCount == 2)

        relay.subscribe()
        relay.subscribe()
        assert(relay.observerCount == 4)
    }

    @Test
    fun testInitialValue() {
        val relay = ActionRelay(4)
        val testObserver = relay.test()
        relay.accept(0)
        testObserver.values().let { result ->
            assert(result.size == 2)
            assert(result == listOf(4, 0))
        }
    }
}
