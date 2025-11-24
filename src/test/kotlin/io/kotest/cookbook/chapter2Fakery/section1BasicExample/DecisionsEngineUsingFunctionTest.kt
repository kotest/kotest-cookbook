package io.kotest.cookbook.chapter2Fakery.section1BasicExample

import io.kotest.cookbook.chapter2Fakery.DecisionsEngineUsingFunction
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DecisionsEngineUsingFunctionTest: StringSpec() {
    private val serviceToTest = DecisionsEngineUsingFunction(
        answer = { 42 }
    )

    init {
        "makes the correct decision" {
            serviceToTest.decide("The weather in Duluth today is?") shouldBe
                    """The decision on "The weather in Duluth today is?" is 42"""
        }
    }
}