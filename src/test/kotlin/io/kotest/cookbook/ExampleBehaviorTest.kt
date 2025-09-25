package io.kotest.cookbook

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

/**
 * Example test demonstrating BehaviorSpec style (Given-When-Then).
 * This style is great for behavior-driven development (BDD).
 */
class ExampleBehaviorTest : BehaviorSpec({
    
    given("a calculator") {
        val calculator = Calculator()
        
        `when`("adding two positive numbers") {
            val result = calculator.add(3, 4)
            
            then("the result should be their sum") {
                result shouldBe 7
            }
        }
        
        `when`("dividing by zero") {
            then("it should throw an exception") {
                try {
                    calculator.divide(10, 0)
                    throw AssertionError("Expected ArithmeticException")
                } catch (e: ArithmeticException) {
                    e.message shouldBe "Division by zero"
                }
            }
        }
    }
    
    given("string utilities") {
        `when`("checking if 'racecar' is a palindrome") {
            val result = StringUtils.isPalindrome("racecar")
            
            then("it should return true") {
                result shouldBe true
            }
        }
        
        `when`("counting words in 'hello world test'") {
            val result = StringUtils.wordCount("hello world test")
            
            then("it should return 3") {
                result shouldBe 3
            }
        }
    }
})