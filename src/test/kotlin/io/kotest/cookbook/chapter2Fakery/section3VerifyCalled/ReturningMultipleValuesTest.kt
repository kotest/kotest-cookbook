package io.kotest.cookbook.chapter2Fakery.section3VerifyCalled

import io.kotest.assertions.playback.toFunction
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.cookbook.chapter2Fakery.AnsweringService
import io.kotest.cookbook.chapter2Fakery.DecisionsEngineUsingFunction
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ReturningMultipleValuesTest: StringSpec() {
    init {
        "returning multiple values via a mock" {
            val mockService = run {
                val service = mockk<AnsweringService>()
                every { service.answer(any()) } returns 42 andThen 43 andThen 44
                service
            }
            mockService.answer("Any question?") shouldBe 42
            mockService.answer("Any question?") shouldBe 43
            mockService.answer("Any question?") shouldBe 44
        }
        "returning multiple values via a test double" {
            // toFunction is an extension function in Kotest's fakery
            val answers = sequenceOf(42, 43, 44).toFunction()
            answers.next() shouldBe 42
            answers.next() shouldBe 43
            answers.next() shouldBe 44
            // Inject this test double as a dependency as follows:
            val decisionsEngine = DecisionsEngineUsingFunction(
                answer = { answers.next() }
            )
        }
        "returning multiple values via a test double with side effects" {
            val answers = sequence {
                println("Side effect before yielding 42")
                yield(42)
                println("Side effect before yielding 43")
                yield(43)
            }.toFunction()
            (answers.next() shouldBe 42).also { println("Next value was: $it") }
            (answers.next() shouldBe 43).also { println("Next value was: $it") }
            /*
Output:
Side effect before yielding 42
Next value was: 42
Side effect before yielding 43
Next value was: 43
             */
        }
        "returning multiple values or throwing an Exception via a test double" {
            val exceptionToThrow = Exception("Oops!")
            // toFunction is an extension function in Kotest's fakery
            val answers = sequenceOf(
                Result.success(42),
                Result.failure(exceptionToThrow),
                Result.success(44),
            ).toFunction()
            answers.next() shouldBe 42
            shouldThrow<Exception> { answers.next() } shouldBe exceptionToThrow
            answers.next() shouldBe 44
        }
    }
}