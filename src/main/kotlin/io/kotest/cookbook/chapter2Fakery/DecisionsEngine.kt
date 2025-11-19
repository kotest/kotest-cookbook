package io.kotest.cookbook.chapter2Fakery

class DecisionsEngine(
    private val answeringService: AnsweringService,
) {
    fun decide(question: String): String {
        return """The decision on "$question" is ${answeringService.answer(question)}"""
    }
}