package io.kotest.cookbook.chapter2Fakery

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DecisionsEngineTest: StringSpec() {
    private val answeringService: AnsweringService = run {
        val ret = mockk<AnsweringService>()
        every { ret.answer(any()) } returns 42
        ret
    }

    private val decisionsEngine = DecisionsEngine(answeringService)

    init {
        "decisionsEngine works" {
            decisionsEngine.decide("The weather in Duluth today is?") shouldBe
                    """The decision on "The weather in Duluth today is?" is 42"""
        }
    }
}