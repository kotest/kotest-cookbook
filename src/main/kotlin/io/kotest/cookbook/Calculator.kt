package io.kotest.cookbook

/**
 * A simple calculator class to demonstrate basic operations.
 * This class serves as an example of how to structure source code in a Kotest cookbook project.
 */
class Calculator {
    
    /**
     * Adds two integers.
     * @param a first number
     * @param b second number
     * @return sum of a and b
     */
    fun add(a: Int, b: Int): Int = a + b
    
    /**
     * Subtracts second number from first.
     * @param a first number
     * @param b second number
     * @return difference of a and b
     */
    fun subtract(a: Int, b: Int): Int = a - b
    
    /**
     * Multiplies two integers.
     * @param a first number
     * @param b second number
     * @return product of a and b
     */
    fun multiply(a: Int, b: Int): Int = a * b
    
    /**
     * Divides first number by second.
     * @param a dividend
     * @param b divisor
     * @return quotient of a and b
     * @throws ArithmeticException if b is zero
     */
    fun divide(a: Int, b: Int): Int {
        if (b == 0) throw ArithmeticException("Division by zero")
        return a / b
    }
}