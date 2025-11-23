package io.kotest.cookbook.chapter2Fakery

import java.util.concurrent.atomic.AtomicBoolean

fun interface ProcessTask {
    operator fun invoke(task: String)
}

class CancellableTaskProcessor(
    private val processTask: ProcessTask,
) {
    private val isCancelledRef = AtomicBoolean(false)

    fun processTasks(tasks: Sequence<String>) {
        for (task in tasks) {
            if (isCancelledRef.get()) {
                println("Processing cancelled. Exiting loop.")
                break
            }
            processTask(task)
        }
    }

    fun cancel() = isCancelledRef.set(true)
}