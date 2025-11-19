package io.kotest.cookbook.chapter1Assertions.section1DataClasses

import io.kotest.assertions.assertSoftly
import io.kotest.cookbook.chapter1Assertions.Box
import io.kotest.cookbook.chapter1Assertions.Box.Companion.withOrderedDimensions
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.time.Instant

class BoxTest1: StringSpec() {
    val originalBox = Box(
        barcode = "12345",
        label = "Misc. Stuff",
        length = 1,
        width = 2,
        height = 3,
        createdAt = Instant.MIN,
    )

    val actual = originalBox.withOrderedDimensions()

    init {
        "should sort dimensions" {
            assertSoftly(actual) {
                listOf(length, width, height) shouldContainExactlyInAnyOrder listOf(
                    originalBox.length,
                    originalBox.width,
                    originalBox.height,
                    )
                length shouldBeGreaterThanOrEqual width
                width shouldBeGreaterThanOrEqual height
            }
        }
        "should copy other fields as is" {
            assertSoftly(actual) {
                barcode shouldBe originalBox.barcode
                label shouldBe originalBox.label
                createdAt shouldBe originalBox.createdAt
            }
        }
    }
}