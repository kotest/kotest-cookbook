# Kotest Cookbook

A comprehensive collection of practical recipes and examples for [Kotest](https://kotest.io/) users. This project demonstrates best practices, testing patterns, and various Kotest features in a well-structured Kotlin library.

## 📚 What's Inside

This cookbook provides:
- **Real-world examples** of Kotest testing patterns
- **Multiple test styles** (StringSpec, FunSpec, BehaviorSpec, and more)
- **Property-based testing** examples
- **Assertion libraries** and matchers
- **Project structure** best practices for Kotlin libraries
- **Build configuration** with Gradle and Kotlin DSL

## 🏗️ Project Structure

```
kotest-cookbook/
├── src/
│   ├── main/kotlin/io/kotest/cookbook/
│   │   ├── Calculator.kt          # Simple calculator class
│   │   └── StringUtils.kt         # String utility functions
│   └── test/kotlin/io/kotest/cookbook/
│       ├── CalculatorTest.kt      # StringSpec examples
│       ├── StringUtilsTest.kt     # FunSpec examples
│       └── ExampleBehaviorTest.kt # BehaviorSpec examples
├── build.gradle.kts               # Kotlin DSL build configuration
├── gradle.properties              # Gradle properties
├── settings.gradle.kts            # Project settings
└── gradlew / gradlew.bat         # Gradle wrapper scripts
```

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- No need to install Gradle (uses Gradle wrapper)

### Building the Project

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run tests with detailed output
./gradlew test --info

# Generate test report
./gradlew test --continue
# Report available at: build/reports/tests/test/index.html
```

## 🧪 Test Examples

### StringSpec Style
Simple, straightforward test definitions:

```kotlin
class CalculatorTest : StringSpec({
    val calculator = Calculator()
    
    "addition should work correctly" {
        calculator.add(2, 3) shouldBe 5
        calculator.add(-1, 1) shouldBe 0
    }
    
    "division by zero should throw ArithmeticException" {
        shouldThrow<ArithmeticException> {
            calculator.divide(10, 0)
        }
    }
})
```

### FunSpec Style
Organized tests with context and nested structure:

```kotlin
class StringUtilsTest : FunSpec({
    context("String reversal") {
        test("should reverse simple strings") {
            StringUtils.reverse("hello") shouldBe "olleh"
        }
        
        test("reversing twice should return original string") {
            checkAll(Arb.string(0..100)) { str ->
                StringUtils.reverse(StringUtils.reverse(str)) shouldBe str
            }
        }
    }
})
```

### BehaviorSpec Style
Behavior-driven development (BDD) with Given-When-Then:

```kotlin
class ExampleBehaviorTest : BehaviorSpec({
    given("a calculator") {
        val calculator = Calculator()
        
        `when`("adding two positive numbers") {
            val result = calculator.add(3, 4)
            
            then("the result should be their sum") {
                result shouldBe 7
            }
        }
    }
})
```

## 🔬 Property-Based Testing

Examples of property-based testing with Kotest:

```kotlin
"addition should be commutative" {
    checkAll(Arb.int(-1000..1000), Arb.int(-1000..1000)) { a, b ->
        calculator.add(a, b) shouldBe calculator.add(b, a)
    }
}
```

## 📦 Dependencies

This project uses the following Kotest modules:

- `kotest-runner-junit5` - JUnit 5 integration
- `kotest-assertions-core` - Core assertion library
- `kotest-property` - Property-based testing

## 🛠️ Development

### IDE Setup

This project works well with:
- **IntelliJ IDEA** (recommended) - Full Kotlin and Kotest support
- **Eclipse** with Kotlin plugin
- **VS Code** with Kotlin extensions

### Running Individual Tests

```bash
# Run specific test class
./gradlew test --tests "*CalculatorTest"

# Run specific test method
./gradlew test --tests "*CalculatorTest.addition should work correctly"

# Run tests matching pattern
./gradlew test --tests "*StringUtils*"
```

### Continuous Testing

For continuous testing during development:

```bash
./gradlew test --continuous
```

## 📖 Learning Resources

- [Kotest Documentation](https://kotest.io/)
- [Kotest GitHub Repository](https://github.com/kotest/kotest)
- [Kotlin Testing Guide](https://kotlinlang.org/docs/jvm-test-using-junit.html)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests with:
- New testing patterns and examples
- Additional Kotest features demonstrations
- Documentation improvements
- Bug fixes

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
