package io.kotest.cookbook

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * Test class demonstrating FunSpec style and string testing patterns.
 */
class StringUtilsTest : FunSpec({
    
    context("String reversal") {
        test("should reverse simple strings") {
            StringUtils.reverse("hello") shouldBe "olleh"
            StringUtils.reverse("world") shouldBe "dlrow"
            StringUtils.reverse("") shouldBe ""
        }
        
        test("should handle single characters") {
            StringUtils.reverse("a") shouldBe "a"
            StringUtils.reverse("1") shouldBe "1"
        }
        
        test("reversing twice should return original string") {
            checkAll(Arb.string(0..100)) { str ->
                StringUtils.reverse(StringUtils.reverse(str)) shouldBe str
            }
        }
    }
    
    context("Palindrome detection") {
        test("should detect simple palindromes") {
            StringUtils.isPalindrome("racecar") shouldBe true
            StringUtils.isPalindrome("level") shouldBe true
            StringUtils.isPalindrome("a") shouldBe true
            StringUtils.isPalindrome("") shouldBe true
        }
        
        test("should detect non-palindromes") {
            StringUtils.isPalindrome("hello") shouldBe false
            StringUtils.isPalindrome("world") shouldBe false
            StringUtils.isPalindrome("ab") shouldBe false
        }
        
        test("should ignore case and non-alphanumeric characters") {
            StringUtils.isPalindrome("A man, a plan, a canal: Panama") shouldBe true
            StringUtils.isPalindrome("race a car") shouldBe false
            StringUtils.isPalindrome("Was it a rat I saw?") shouldBe true
        }
    }
    
    context("Word counting") {
        test("should count words correctly") {
            StringUtils.wordCount("hello world") shouldBe 2
            StringUtils.wordCount("one") shouldBe 1
            StringUtils.wordCount("") shouldBe 0
            StringUtils.wordCount("   ") shouldBe 0
        }
        
        test("should handle multiple spaces") {
            StringUtils.wordCount("hello    world") shouldBe 2
            StringUtils.wordCount("  one  two  three  ") shouldBe 3
        }
        
        test("should handle tabs and newlines") {
            StringUtils.wordCount("hello\tworld\ntest") shouldBe 3
        }
    }
})