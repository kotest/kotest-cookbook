package io.kotest.cookbook.chapter2Fakery.section1BasicExample

import io.kotest.cookbook.chapter2Fakery.DecisionsEngineUsingInterface
import io.kotest.cookbook.chapter2Fakery.HasAnswer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DecisionsEngineUsingInterfaceTest : StringSpec() {
    private val serviceToTest = DecisionsEngineUsingInterface(
        hasAnswer = object: HasAnswer {
            override fun answer(question: String): Int = 42
        }
    )

    init {
        "makes the correct decision" {
            serviceToTest.decide("The weather in Duluth today is?") shouldBe
                    """The decision on "The weather in Duluth today is?" is 42"""
        }
    }
}