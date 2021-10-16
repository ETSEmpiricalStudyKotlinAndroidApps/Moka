package io.github.tonnyl.moka

import io.tonnyl.moka.common.getPlatformName
import kotlin.test.Test
import kotlin.test.assertTrue

class IosGreetingTest {

    @Test
    fun testExample() {
        assertTrue(getPlatformName().contains("iOS"), "Check iOS is mentioned")
    }
}