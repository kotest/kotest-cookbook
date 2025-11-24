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
        if(question.contains("apple", ignoreCase = true) &&
            question.contains("orange", ignoreCase = true)) {
            alert(AlertSeverity.MEDIUM, "Should not compare apples and oranges, but did: $question")
        }
        return """The decision on "$question" is ${answer(question)}"""
    }
}