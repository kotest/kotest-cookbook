package io.kotest.cookbook.chapter2Fakery

enum class AlertSeverity {
    LOW, MEDIUM, HIGH
}

fun interface Alert {
    operator fun invoke(severity: AlertSeverity, message: String)
}

// Typically this class would be annotated with @Service or another similar annotation
class DecisionsEngineWithAlerting(
    private val answer: Answer,
    private val alert: Alert,
) {
    fun decide(question: String): String {
        return """The decision on "$question" is ${answer(question)}"""
    }
}