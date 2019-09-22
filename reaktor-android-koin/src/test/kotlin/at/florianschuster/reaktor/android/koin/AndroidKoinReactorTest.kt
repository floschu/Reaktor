package at.florianschuster.reaktor.android.koin

import at.florianschuster.reaktor.android.ViewModelReactor
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest

class AndroidKoinReactorTest : AutoCloseKoinTest() {

    private class TestReactor : ViewModelReactor<Unit, Unit, Unit>(Unit)

    private val testModule: Module = module {
        reactor { TestReactor() }
    }

    @Before
    fun before() {
        startKoin {
            modules(testModule)
        }
    }

    @Test
    fun testModuleReactorDSLExtension() {
        get<TestReactor>()
    }
}
