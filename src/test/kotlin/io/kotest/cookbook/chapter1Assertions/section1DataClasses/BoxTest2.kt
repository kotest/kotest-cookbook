package io.kotest.cookbook.chapter1Assertions.section1DataClasses

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.cookbook.chapter1Assertions.Box
import io.kotest.cookbook.chapter1Assertions.Box.Companion.withOrderedDimensions
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.time.Instant

class BoxTest2: StringSpec() {
    private val originalBox = Box(
        barcode = "12345",
        label = "Misc. Stuff",
        length = 1,
        width = 2,
        height = 3,
        createdAt = Instant.MIN,
    )

    private val actual = originalBox.withOrderedDimensions()

    init {
        "withOrderedDimensions works" {
            assertSoftly(actual) {
                withClue("dimensions match original ones with possibly different order") {
                    listOf(length, width, height) shouldContainExactlyInAnyOrder listOf(
                        originalBox.length,
                        originalBox.width,
                        originalBox.height,
                     )
                }
                withClue("dimensions are sorted") {
                    length shouldBeGreaterThanOrEqual width
                    width shouldBeGreaterThanOrEqual height
                }
                withClue("other fields are copied as is") {
                    barcode shouldBe originalBox.barcode
                    label shouldBe originalBox.label
                    createdAt shouldBe originalBox.createdAt
                }
            }
        }
    }
}