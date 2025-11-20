package io.kotest.cookbook.chapter2Fakery

interface HasAnswer {
    fun answer(question: String): Int
}

// Typically this class would be annotated with @Service or another similar annotation
class AnsweringServiceV2 : HasAnswer {
    override fun answer(question: String): Int {
        TODO()
    }

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