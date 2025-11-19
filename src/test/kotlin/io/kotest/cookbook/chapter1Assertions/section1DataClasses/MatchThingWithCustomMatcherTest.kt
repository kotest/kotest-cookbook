package io.kotest.cookbook.chapter1Assertions.section1DataClasses

import io.kotest.cookbook.chapter1Assertions.Thing
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.equality.matchDoublesWithTolerance

class MatchThingWithCustomMatcherTest: StringSpec() {
    private val apple = Thing(name = "apple", weight = 1.5)
    private val anotherApple = Thing(name = "apple", weight = 1.501)

    init {
        "should match things with custom matcher" {
            apple shouldBeEqualUsingFields {
                overrideMatchers = mapOf(
                    Thing::weight to matchDoublesWithTolerance(0.01)
                )
                anotherApple
            }
        }
    }
}