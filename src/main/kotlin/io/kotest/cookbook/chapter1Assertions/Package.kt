package io.kotest.cookbook.chapter1Assertions

import java.time.Instant

data class Box(
    val barcode: String,
    val length: Int,
    val width: Int,
    val height: Int,
    val label: String,
    val createdAt: Instant,
)