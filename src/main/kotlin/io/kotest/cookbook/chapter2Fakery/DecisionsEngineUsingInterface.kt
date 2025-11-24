package io.kotest.cookbook.chapter2Fakery

// This class can be annotated with @Service or @Component or another similar annotation
class DecisionsEngineUsingInterface(
    private val hasAnswer: HasAnswer, // SpringBoot can inject this dependency
) {
    fun decide(question: String): String {
        return """The decision on "$question" is ${hasAnswer.answer(question)}"""
    }
}