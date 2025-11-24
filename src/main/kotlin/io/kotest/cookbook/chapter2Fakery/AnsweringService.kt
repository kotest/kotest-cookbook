package io.kotest.cookbook.chapter2Fakery

// If we are using a DI framework such as SpringBoot,
// then this class would be annotated with @Service or another similar annotation
class AnsweringService {
    fun answer(question: String): Int {
        TODO()
    }

    // these CRUD methods below are of course necessary,
    // but they are not used anywhere in `DecisionsEngine`
    fun saveFact(id: Int, fact: String) {
        TODO()
    }

    fun getFact(id: Int): String {
        TODO()
    }

    fun deleteFact(id: Int) {
        TODO()
    }
}