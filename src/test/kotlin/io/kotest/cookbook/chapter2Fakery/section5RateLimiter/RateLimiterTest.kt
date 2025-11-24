package io.kotest.cookbook.chapter2Fakery.section5RateLimiter

import io.kotest.assertions.playback.toFunction
import io.kotest.assertions.withClue
import io.kotest.cookbook.chapter2Fakery.RateLimiter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import java.time.Instant

class RateLimiterTest: StringSpec() {
    init {
        "should delay when calls last less than allowed frequency" {
            val externalServiceCalls = mutableListOf<String>()
            val instances = sequenceOf(100L, 150L, 200L, 210L)
                .map { Instant.ofEpochMilli(it) }
                .toFunction()
            val delays = mutableListOf<Long>()
            val rateLimiter = RateLimiter(
                externalServiceCall = { request ->
                    externalServiceCalls.add(request)
                },
                allowedFrequencyInMilliseconds = 100,
                getNow = { instances.next() },
                delayFor = { delayForMillis ->
                    delays.add(delayForMillis)
                }
            )
            rateLimiter.callService(
                sequenceOf("task1", "task2")
            )
            withClue("should process all tasks in order") {
                externalServiceCalls shouldContainExactly listOf("task1", "task2")
            }
            withClue("should have correct delays") {
                delays shouldContainExactly listOf(50L, 90L)
            }
        }
        "should not delay when calls last longer than allowed frequency" {
            val externalServiceCalls = mutableListOf<String>()
            val instances = sequenceOf(100L, 210L, 220L, 320L)
                .map { Instant.ofEpochMilli(it) }
                .toFunction()
            val delays = mutableListOf<Long>()
            val rateLimiter = RateLimiter(
                externalServiceCall = { request ->
                    externalServiceCalls.add(request)
                },
                allowedFrequencyInMilliseconds = 100,
                getNow = { instances.next() },
                delayFor = { delayForMillis ->
                    delays.add(delayForMillis)
                }
            )
            rateLimiter.callService(
                sequenceOf("task1", "task2")
            )
            withClue("should process all tasks in order") {
                externalServiceCalls shouldContainExactly listOf("task1", "task2")
            }
            withClue("should not delay because calls took longer than allowed frequency") {
                delays.shouldBeEmpty()
            }
        }
    }
}