package io.kotest.cookbook

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

/**
 * Test class demonstrating various Kotest testing styles and features.
 * Uses StringSpec style for simple, readable tests.
 */
class CalculatorTest : StringSpec({
    
    val calculator = Calculator()
    
    "addition should work correctly" {
        calculator.add(2, 3) shouldBe 5
        calculator.add(-1, 1) shouldBe 0
        calculator.add(0, 0) shouldBe 0
    }
    
    "subtraction should work correctly" {
        calculator.subtract(5, 3) shouldBe 2
        calculator.subtract(1, 1) shouldBe 0
        calculator.subtract(-1, -1) shouldBe 0
    }
    
    "multiplication should work correctly" {
        calculator.multiply(3, 4) shouldBe 12
        calculator.multiply(-2, 3) shouldBe -6
        calculator.multiply(0, 100) shouldBe 0
    }
    
    "division should work correctly" {
        calculator.divide(10, 2) shouldBe 5
        calculator.divide(7, 3) shouldBe 2 // Integer division
        calculator.divide(-6, 2) shouldBe -3
    }
    
    "division by zero should throw ArithmeticException" {
        shouldThrow<ArithmeticException> {
            calculator.divide(10, 0)
        }
    }
    
    "addition should be commutative (property-based test)" {
        checkAll(Arb.int(-1000..1000), Arb.int(-1000..1000)) { a, b ->
            calculator.add(a, b) shouldBe calculator.add(b, a)
        }
    }
    
    "multiplication should be associative (property-based test)" {
        checkAll(Arb.int(-100..100), Arb.int(-100..100), Arb.int(-100..100)) { a, b, c ->
            calculator.multiply(calculator.multiply(a, b), c) shouldBe calculator.multiply(a, calculator.multiply(b, c))
        }
    }
})