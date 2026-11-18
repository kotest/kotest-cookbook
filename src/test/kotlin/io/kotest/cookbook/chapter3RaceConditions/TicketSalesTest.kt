package io.kotest.cookbook.chapter3RaceConditions

import io.kotest.core.spec.style.StringSpec
import io.kotest.raceConditions.ParallelRunner.Companion.runInParallel
import java.util.concurrent.atomic.AtomicReference

class TicketSalesTest : StringSpec() {
    private val tickets = (0..9999).map { Ticket(it) }

    init {
        "show that this implementation is not thread-safe" {
            val boughtByFirstAgent = mutableListOf<Ticket>()
            val boughtBySecondAgent = mutableListOf<Ticket>()
            runInParallel(
                { runner ->
                    tickets.forEach { ticket ->
                        runner.await()
                        if(ticket.sellTo("Agent-1")) {
                            boughtByFirstAgent.add(ticket)
                        }
                    }
                },
                { runner ->
                    tickets.forEach { ticket ->
                        runner.await()
                        if(ticket.sellTo("Agent-2")) {
                            boughtBySecondAgent.add(ticket)
                        }
                    }
                }
            )
            val duplicates = boughtByFirstAgent.map { it.seat }
                .intersect(boughtBySecondAgent.map { it.seat }.toSet())
            println("Duplicates count: ${duplicates.size}")
            println(duplicates)
        }
    }
}

private data class Ticket(
    val seat: Int,
) {
    private val soldTo: AtomicReference<String?> = AtomicReference(null)

    fun isAvailable(): Boolean {
        Thread.sleep(1L) // mimic the duration of a DB call and some time to make a decision
        return soldTo.get() == null
    }

    // this implementation uses a thread safe AtomicReference, but the whole thing is not thread-safe
    fun sellTo(customer: String): Boolean {
        if(isAvailable()) {
            soldTo.set(customer)
            return true
        }
        return false
    }
}