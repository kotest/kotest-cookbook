package io.kotest.cookbook.chapter2Fakery.section3VerifyCalled

import io.kotest.cookbook.chapter2Fakery.DecisionsEngineUsingFunction
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain

class VerifyingTestDoubleWasCalledTest: StringSpec() {
    init {
        "verifying test double was called" {
            var callCount = 0
            val systemToTest = DecisionsEngineUsingFunction(
                answer = { question : String ->
                    question.shouldNotContain("apple")
                    callCount++
                    42
                }
            )
            systemToTest.decide("Do oranges taste better than bananas?")
            callCount shouldBe 1
        }
        "test" {
            listOf<Int>().shouldBeEmpty()
        }
    }
}