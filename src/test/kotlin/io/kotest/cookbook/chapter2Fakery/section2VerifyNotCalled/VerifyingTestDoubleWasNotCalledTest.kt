package io.kotest.cookbook.chapter2Fakery.section2VerifyNotCalled

import io.kotest.assertions.AssertionErrorBuilder.Companion.failSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.cookbook.chapter2Fakery.AlertSeverity
import io.kotest.cookbook.chapter2Fakery.DecisionsEngineWithAlerting
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class VerifyingTestDoubleWasNotCalledTest: StringSpec() {
    init {
        "alerting should not be called - fail the first time it does".config(enabled = false) {
            val serviceToTest = DecisionsEngineWithAlerting(
                answer = { 42 },
                alert = { severity: AlertSeverity, message: String ->
                    failSoftly("Alert was called with severity $severity and message: $message")
                }
            )
            shouldNotThrowAny {
                serviceToTest.decide("Do apples taste better than oranges?")
            }
        }
        "alerting should not be called - collect all the calls for further analysis".config(enabled = false) {
            val alertingCalls = mutableListOf<Pair<AlertSeverity, String>>()
            val serviceToTest = DecisionsEngineWithAlerting(
                answer = { 42 },
                alert = { severity: AlertSeverity, message: String ->
                    alertingCalls.add(Pair(severity, message))
                }
            )
            serviceToTest.decide("Do apples taste better than oranges?")
            serviceToTest.decide("Do oranges taste better than apples?")
            alertingCalls shouldBe listOf()
        }
    }
}