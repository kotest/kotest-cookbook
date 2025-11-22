package io.kotest.cookbook.chapter2Fakery.section3VerifyCalled

import io.kotest.assertions.withClue
import io.kotest.cookbook.chapter2Fakery.Container
import io.kotest.cookbook.chapter2Fakery.ElementsProcessorWithFunctionDependency
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.ranges.shouldBeIn

class ElementsProcessorTestWithTestDoubles: StringSpec() {
    init {
        "splits elements into correct chunks and calls processor" {
            val calls = mutableListOf<Container>()
            val serviceToTest = ElementsProcessorWithFunctionDependency(
                processContainer = { container: Container ->
                    calls.add(container)
                },
                maxChunkSize = 2,
            )
            val elements = listOf(1, 2, 3, 4, 5)
            serviceToTest.process(elements)
            withClue("each element is in exactly one container") {
                val allElements = calls.flatMap { it.elements }
                allElements shouldContainExactlyInAnyOrder elements
            }
            withClue("elements are correctly chunked") {
                calls.forAll { container ->
                    container.elements.size shouldBeIn 1..2
                }
            }
        }
    }
}