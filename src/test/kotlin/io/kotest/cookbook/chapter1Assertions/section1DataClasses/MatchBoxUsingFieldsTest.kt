package io.kotest.cookbook.chapter1Assertions.section1DataClasses

import io.kotest.cookbook.chapter1Assertions.Box
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import java.time.Instant

class MatchBoxUsingFieldsTest: StringSpec() {
    val box = Box(
        barcode = "123456789",
        length = 10,
        width = 5,
        height = 2,
        label = "Stuff",
        createdAt = Instant.ofEpochMilli(123L),
    )

    init {
        "match boxes ignoring createdAt field" {
            val anotherBox = box.copy(createdAt = Instant.ofEpochMilli(1234L))
            box shouldBeEqualUsingFields {
                excludedProperties = setOf(Box::createdAt)
                anotherBox
            }
        }
    }
}