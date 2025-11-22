package io.kotest.cookbook.chapter2Fakery.section3VerifyCalled

import io.kotest.cookbook.chapter2Fakery.Container
import io.kotest.cookbook.chapter2Fakery.ContainerPrinter
import io.kotest.cookbook.chapter2Fakery.ElementsProcessorWithObjectDependency
import io.kotest.core.spec.style.StringSpec
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class ElementsProcessorTestWithMocks: StringSpec() {
    private val containerProcessor = run {
        val ret = mockk<ContainerPrinter>()
        justRun { ret.process(any()) }
        ret
    }

    init {
        "factory calls containerProcessor with correct arguments" {
            val factory = ElementsProcessorWithObjectDependency(
                containerProcessor,
                maxChunkSize = 2,
            )
            factory.process(listOf(1, 2, 3, 4, 5))
            // Here we are verifying how the factory is implemented,
            // not that it meets the requirements.
            verify(exactly = 1) { containerProcessor.process(Container(listOf(1, 2))) }
            verify(exactly = 1) { containerProcessor.process(Container(listOf(3, 4))) }
            verify(exactly = 1) { containerProcessor.process(Container(listOf(5))) }
        }
    }
}