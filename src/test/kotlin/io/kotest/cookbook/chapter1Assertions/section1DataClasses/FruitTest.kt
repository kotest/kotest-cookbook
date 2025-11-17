package io.kotest.cookbook.chapter1Assertions.section1DataClasses

import io.kotest.cookbook.chapter1Assertions.largeRedSweetApple
import io.kotest.cookbook.chapter1Assertions.largeRedTartApple
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.shouldBe


class FruitTest: StringSpec() {
    init {
        "shouldBe does detect the difference".config(enabled = false) {
            largeRedSweetApple shouldBe largeRedTartApple
        }
        "shouldBeEqualUsingFields describes the difference in more detail".config(enabled = true) {
            largeRedSweetApple shouldBeEqualUsingFields  largeRedTartApple
        }
    }
}