package io.kotest.cookbook.chapter2Fakery

import kotlinx.coroutines.delay
import java.time.Instant

class RateLimiterWithHardcodedDependencies(
    private val externalServiceCall: ExternalServiceCall,
    private val allowedFrequencyInMilliseconds: Int,
) {
    suspend fun callService(requests: Sequence<String>) {
        requests.forEach { request ->
            val startedAt = Instant.now()
            externalServiceCall(request)
            val endedAt = Instant.now()
            val duration = endedAt.toEpochMilli() - startedAt.toEpochMilli()
            if(duration < allowedFrequencyInMilliseconds) {
                val sleepTime = allowedFrequencyInMilliseconds - duration
                delay(sleepTime)
            }
        }
    }
}