package io.kotest.cookbook

/**
 * Utility functions for string manipulation.
 * This demonstrates organizing utility functions in a companion object.
 */
class StringUtils {
    companion object {
        /**
         * Reverses a string.
         * @param input the string to reverse
         * @return reversed string
         */
        fun reverse(input: String): String = input.reversed()
        
        /**
         * Checks if a string is a palindrome.
         * @param input the string to check
         * @return true if the string is a palindrome, false otherwise
         */
        fun isPalindrome(input: String): Boolean {
            val cleaned = input.lowercase().filter { it.isLetterOrDigit() }
            return cleaned == cleaned.reversed()
        }
        
        /**
         * Counts words in a string.
         * @param input the string to count words in
         * @return number of words
         */
        fun wordCount(input: String): Int {
            return input.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        }
    }
}