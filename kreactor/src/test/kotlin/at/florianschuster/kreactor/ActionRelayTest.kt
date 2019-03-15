package at.florianschuster.kreactor

import org.amshove.kluent.shouldEqual
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
            result.size shouldEqual 3
            result shouldEqual listOf(0, 1, 2)
        }
    }

    @Test
    fun testObservers() {
        val relay = ActionRelay<Int>()
        relay.observerCount shouldEqual 0

        relay.subscribe()
        relay.observerCount shouldEqual 1

        relay.subscribe()
        relay.observerCount shouldEqual 2

        relay.subscribe()
        relay.subscribe()
        relay.observerCount shouldEqual 4
    }

}