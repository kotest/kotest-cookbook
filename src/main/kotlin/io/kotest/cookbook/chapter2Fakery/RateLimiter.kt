package io.kotest.cookbook.chapter2Fakery

import java.time.Instant
import kotlinx.coroutines.delay

fun interface ExternalServiceCall {
    operator fun invoke(request: String)
}

fun interface GetNow {
    operator fun invoke(): Instant
}

fun interface DelayFor {
    suspend operator fun invoke(milliseconds: Long)
}

// wiring up RateLimiter manually can be done like this:
fun getRateLimiter(
    externalServiceCall: ExternalServiceCall,
    allowedFrequencyInMilliseconds: Int,
) = RateLimiter(
    externalServiceCall,
    allowedFrequencyInMilliseconds,
    getNow = { Instant.now() },
    delayFor = { delayForMillis -> delay(delayForMillis) }
)

class RateLimiter(
    private val externalServiceCall: ExternalServiceCall,
    private val allowedFrequencyInMilliseconds: Int,
    private val getNow: GetNow,
    private val delayFor: DelayFor,
) {
    suspend fun callService(requests: Sequence<String>) {
        requests.forEach { request ->
            val startedAt = getNow()
            externalServiceCall(request)
            val endedAt = getNow()
            val duration = endedAt.toEpochMilli() - startedAt.toEpochMilli()
            if(duration < allowedFrequencyInMilliseconds) {
                val sleepTime = allowedFrequencyInMilliseconds - duration
                delayFor(sleepTime)
            }
        }
    }
}