package io.kotest.cookbook.chapter1Assertions

import java.time.Instant

data class Box(
    val barcode: String,
    val label: String,
    val length: Int,
    val width: Int,
    val height: Int,
    val createdAt: Instant,
) {
    companion object {
        fun Box.withOrderedDimensions(): Box {
            val sortedDimensions = listOf(length, width, height).sorted()
            return this.copy(
                length = sortedDimensions[2],
                width = sortedDimensions[1],
                height = sortedDimensions[0],
            )
        }
    }
}