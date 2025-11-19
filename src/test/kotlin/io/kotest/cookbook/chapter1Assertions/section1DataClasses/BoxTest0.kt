package io.kotest.cookbook.chapter1Assertions.section1DataClasses

import io.kotest.cookbook.chapter1Assertions.Box
import io.kotest.cookbook.chapter1Assertions.Box.Companion.withOrderedDimensions
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import java.time.Instant

class BoxTest0: StringSpec() {
    val originalBox = Box(
        barcode = "12345",
        label = "Misc. Stuff",
        length = 1,
        width = 2,
        height = 3,
        createdAt = Instant.MIN,
    )

    init {
        "should sort dimensions" {
            originalBox.withOrderedDimensions() shouldBeEqualUsingFields Box(
                barcode = "12345",
                label = "Misc. Stuff",
                length = 3,
                width = 2,
                height = 1,
                createdAt = Instant.MIN,
            )
        }
    }
}