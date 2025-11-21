package io.kotest.cookbook.chapter2Fakery.section3VerifyCalled

import io.kotest.cookbook.chapter2Fakery.Container
import io.kotest.cookbook.chapter2Fakery.ContainerElementsPrinter
import io.kotest.cookbook.chapter2Fakery.ContainerFactoryWithObject
import io.kotest.core.spec.style.StringSpec
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class ContainerFactoryTestWithMocks: StringSpec() {
    private val containerProcessor = run {
        val ret = mockk<ContainerElementsPrinter>()
        justRun { ret.process(any()) }
        ret
    }

    init {
        "factory calls containerProcessor with correct arguments" {
            val factory = ContainerFactoryWithObject(
                containerProcessor,
                maxSize = 2,
            )
            factory.processInChunks(listOf(1, 2, 3, 4, 5))
            // Here we are verifying how the factory is implemented,
            // not that it meets the requirements.
            verify(exactly = 1) { containerProcessor.process(Container(listOf(1, 2))) }
            verify(exactly = 1) { containerProcessor.process(Container(listOf(3, 4))) }
            verify(exactly = 1) { containerProcessor.process(Container(listOf(5))) }
        }
    }
}