package io.kotest.cookbook.chapter2Fakery

fun interface Answer {
    operator fun invoke(question: String): Int
}

// If we are wiring up dependencies manually, we can use this function:
// If we are using a DI framework, such as SpringBoot, then the framework should handle dependency injection.
fun getDecisionsEngine(answeringService: AnsweringService): DecisionsEngineUsingFunction =
    DecisionsEngineUsingFunction(answeringService::answer)

class DecisionsEngineUsingFunction(
    private val answer: Answer,
) {
    fun decide(question: String): String {
        return """The decision on "$question" is ${answer(question)}"""
    }
}