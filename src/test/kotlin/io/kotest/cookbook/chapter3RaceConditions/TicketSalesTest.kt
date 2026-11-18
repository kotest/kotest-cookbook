package io.kotest.cookbook.chapter3RaceConditions

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.raceConditions.ParallelRunner.Companion.runInParallel
import java.util.concurrent.atomic.AtomicReference

class TicketSalesTest : StringSpec() {

    init {
        "show that Ticket implementation is not thread-safe" {
            val tickets = (0..9999).map { Ticket(it) }

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
            // occasionally, there will be no duplicates, but usually there are some
            // typical output, which will vary from run to run:
            // Duplicates count: 23
            // [0, 40, 44, 50, 51, 52, 72, 78, 104, 111, 113, 122, 559, 1040, 1184, 1395, 2013, 2097, 2848, 6472, 7973, 8027, 9791]
        }
        "show that ThreadSafeTicket implementation is thread-safe" {
            val tickets = (0..9999).map { ThreadSafeTicket(it) }
            val boughtByFirstAgent = mutableListOf<ThreadSafeTicket>()
            val boughtBySecondAgent = mutableListOf<ThreadSafeTicket>()
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
            duplicates.shouldBeEmpty()
        }
    }
}

// this class is deliberately not thread-safe
private data class Ticket(
    val seat: Int,
) {
    private val soldTo: AtomicReference<String?> = AtomicReference(null)

    internal fun isAvailable(): Boolean {
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

private data class ThreadSafeTicket(
    val seat: Int,
) {
    private var soldTo: String? = null

    private val lock = Any()

    internal fun isAvailable(): Boolean {
        Thread.sleep(1L) // mimic the duration of a DB call and some time to make a decision
        return soldTo == null
    }

    // this implementation uses a thread safe AtomicReference, but the whole thing is not thread-safe
    fun sellTo(customer: String): Boolean {
        synchronized(lock) {
            if(isAvailable()) {
                soldTo = customer
                return true
            }
        }
        return false
    }
}