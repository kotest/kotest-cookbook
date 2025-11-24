package io.kotest.cookbook.chapter2Fakery.section4ExampleCancel

import io.kotest.assertions.playback.PlaybackElements
import io.kotest.assertions.playback.toFunction
import io.kotest.cookbook.chapter2Fakery.CancellableTaskProcessor
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CancelLongRunningLoopTest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerRoot

    private val tasks = sequence<String> {
        yield("task1")
        yield("task2")
    }

    private val results: PlaybackElements<String> = sequence<String> {
        processor.cancel()
        yield("result1")
        yield("result2")
    }.toFunction()

    private val processedTasks = mutableListOf<String>()

    private val processor = CancellableTaskProcessor(
        processTask = { task ->
            processedTasks.add(task)
            results.next()
        },
    )

    init {
        "stops processing tasks when cancelled" {
            processor.processTasks(tasks)
            processedTasks shouldBe listOf("task1")
        }
        "processes all tasks if not cancelled" {
            val allTasks = sequenceOf(
                "task1",
                "task2",
                "task3",
            )
            val allResults: PlaybackElements<String> = sequence<String> {
                yield("result1")
                yield("result2")
                yield("result3")
            }.toFunction()

            val allProcessedTasks = mutableListOf<String>()
            val allProcessor = CancellableTaskProcessor(
                processTask = { task ->
                    allProcessedTasks.add(task)
                    allResults.next()
                },
            )

            allProcessor.processTasks(allTasks)
            allProcessedTasks shouldBe listOf("task1", "task2", "task3")
        }
    }
}