package io.kotest.cookbook.chapter2Fakery

import java.util.concurrent.atomic.AtomicBoolean

fun interface GetTasks {
    operator fun invoke(): Sequence<String>
}

fun interface ProcessTask {
    operator fun invoke(task: String) : String
}

class CancellableTaskProcessor(
    private val processTask: ProcessTask,
) {
    private val isCancelledRef = AtomicBoolean(false)

    fun processTasks(tasks: Sequence<String>) = tasks
        .takeWhile {
        if(isCancelledRef.get()) {
            println("Processing cancelled, stopping task processing.")
        } else {
            println("Processing task: $it")
        }
        !isCancelledRef.get()
    }
        .map { processTask(it) }
        .toList()

    fun cancel() = isCancelledRef.set(true)
}