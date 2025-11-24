package io.kotest.cookbook.chapter2Fakery.section4ExampleCancel

import io.kotest.cookbook.chapter2Fakery.CancellableTaskProcessor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CancelLongRunningLoopTest: StringSpec() {
    private val tasks = sequence<String> {
        yield("task1")
        processor.cancel()
        yield("task2")
    }

    private val processedTasks = mutableListOf<String>()

    private val processor = CancellableTaskProcessor(
        processTask = { task -> processedTasks.add(task) }
    )

    init {
        "stops processing tasks when cancelled" {
            processor.processTasks(tasks)
            processedTasks shouldBe listOf("task1")
        }
    }
}