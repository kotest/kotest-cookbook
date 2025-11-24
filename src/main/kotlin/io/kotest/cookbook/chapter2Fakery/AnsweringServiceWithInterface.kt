package io.kotest.cookbook.chapter2Fakery

class AnsweringServiceWithInterface() : HasAnswer {
    override fun answer(question: String): Int {
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