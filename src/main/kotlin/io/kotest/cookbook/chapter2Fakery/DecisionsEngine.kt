package io.kotest.cookbook.chapter2Fakery

// If we are using a DI framework such as SpringBoot,
// then this class would be annotated with @Service or another similar annotation
class DecisionsEngine(
    private val answeringService: AnsweringService,
) {
    fun decide(question: String): String {
        return """The decision on "$question" is ${answeringService.answer(question)}"""
    }
}