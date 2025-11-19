package io.kotest.cookbook.chapter2Fakery

// Typically this class would be annotated with @Service or another similar annotation
class DecisionsEngine(
    private val answeringService: AnsweringService,
) {
    fun decide(question: String): String {
        return """The decision on "$question" is ${answeringService.answer(question)}"""
    }
}