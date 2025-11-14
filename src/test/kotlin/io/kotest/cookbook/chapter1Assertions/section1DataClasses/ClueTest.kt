package io.kotest.cookbook.chapter1Assertions.section1DataClasses

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ClueTest: StringSpec() {
    init {
        // to run the tests, enable them
        "without clue".config(enabled = false) {
            2*2 shouldBe 5
        }
        "with clue".config(enabled = false) {
            withClue("Example from textbook on page 11") {
                2*2 shouldBe 5
            }
        }
    }
}