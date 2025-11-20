# Kotest Cookbook

There are multiple ways to accomplish most common tasks in Kotest. 
So let's discuss how we choose the right tools for the task, what are their pros and cons, and what trade-offs we should consider when choosing one over another.
<br/>
<br/>
Of course we have the ubiquitous `shouldBe` - the Swiss Army knife of assertions. 
Surely it can handle almost anything, but for better results we typically go for specialized tools instead of that jack-of-all-trades aka `shouldBe`.

<!-- TOC -->
* [Kotest Cookbook](#kotest-cookbook)
  * [Assertions](#assertions)
    * [Matching Data Classes with `shouldBeEqualUsingFields`](#matching-data-classes-with-shouldbeequalusingfields)
    * [Explicitly Matching Fields of Data Classes](#explicitly-matching-fields-of-data-classes)
  * [Using Fakery](#using-fakery)
    * [Basic Example - Replace A Mock with A Test Double](#basic-example---replace-a-mock-with-a-test-double)
    * [Example: Verifying That Test Double Was Not Called](#example-verifying-that-test-double-was-not-called)
    * [Example: Verifying That Test Double Was Called](#example-verifying-that-test-double-was-called)
  * [Learning Resources](#learning-resources)
  * [Contributing](#contributing)
  * [License](#license)
<!-- TOC -->

## Assertions

This is similar to multiple similar tools in a toolbox - even though a Swiss Army knife aka `shouldBe` can handle many comparisons, generally we can get better results using more specialized tools.

### Matching Data Classes with `shouldBeEqualUsingFields`

The ubiquitous `shouldBe` does detect the difference between two objects:

```kotlin
largeRedSweetApple shouldBe largeRedTartApple
```

and the output can be easy to grok in the IDE, especially for simple objects with few fields:

```
data class diff for io.kotest.cookbook.chapter1Assertions.Fruit
└ taste:
Expected :Fruit(name=Apple, color=Red, size=Large, taste=Tart)
Actual   :Fruit(name=Apple, color=Red, size=Large, taste=Sweet)
```

If, however, we are comparing complex objects with many fields, this format of output can be time-consuming to parse.
<br/>
<br/>
For more detailed description of the differences for data classes,  
we can use `shouldBeEqualToComparingFields` and `shouldBeEqualToIgnoringFields` assertions, as follows:

```kotlin
largeRedSweetApple shouldBeEqualUsingFields  largeRedTartApple
```

which generates the output which is much easier to read:

```
Using fields:
 - color
 - name
 - size
 - taste

Fields that differ:
 - taste  =>  expected:<"Tart"> but was:<"Sweet">
```

So `shouldBeEqualUsingFields` exposes differences in a very readable way. But it does not allow to explain why we expect these values. As such, it is a great choice in situations where such explanations are not needed, such as:

* deserialize a message correctly
* correctly map data from one layer to another
* read correct date from the database

It's also a good choice when we need to move quickly and are not overly concerned about long-term maintainability of the tests.
<br/>
<br/>
Long-term maintenance concerns: if/when we add fields to the data class, the tests won't compile.
Good news is that this will give us chance to fix the test accordingly.
If we are testing how data is mapped between layers or formats, this is exactly the right thing to do.
If, however, we are testing something completely unrelated to this new field, we shall still have to fix the expected value, 
which is a bit unproductive and means that we own a high-maintenance test - so we should consider alternatives. 
While high-maintenance tests is an interesting topic and it clearly deserves a detailed discussion, we shall not dive into that rabbit hole here.
<br/>
<br/>
Fields such as timestamps, uuids, and auto-generated ids are commonly ignored in such tests.
To accomplish that, we can customize `shouldBeEqualUsingFields` to provide non-default matchers for some fields or to ignore them altogether.
For instance, the following code ignores `createdAt` field when comparing two objects:

```kotlin
val box = Box(
    barcode = "123456789",
    length = 10,
    width = 5,
    height = 2,
    label = "Stuff",
    createdAt = Instant.ofEpochMilli(123L),
)

val anotherBox = box.copy(createdAt = Instant.ofEpochMilli(1234L))
box shouldBeEqualUsingFields {
  excludedProperties = setOf(Box::createdAt)
  anotherBox
}
```

Sometimes we need to use a custom comparison logic for some fields.
For instance, when comparing floating point numbers computed by some calculations, we may want to use a tolerance value, as follows:

```kotlin
private val apple = Thing(name = "apple", weight = 1.5)
private val anotherApple = Thing(name = "apple", weight = 1.501)

apple shouldBeEqualUsingFields {
  overrideMatchers = mapOf(
    Thing::weight to matchDoublesWithTolerance(0.01)
  )
  anotherApple
}
```

While the ability to ignore fields or override field matchers in `shouldBeEqualUsingFields` is definitely handy, we should not overdo it. 
While it's totally fine to ignore or override one or two fields, if we find ourselves doing that for many fields, we should start considering other approaches.
There are multiple ways to match data classes - it might be easier to just explicitly match the fields we want using the matchers of our choice.
<br/>
<br/>
In the next few examples we shall do just that.

### Explicitly Matching Fields of Data Classes

If we explicitly match fields of data classes, we can explain why we expect exactly these values.
There are multiple ways to do that in Kotest. We shall discuss a few here, most definitely not all of them.
<br/>
<br/>
Suppose we are working with the following data class:

```kotlin
data class Box(
    val barcode: String,
    val label: String,
    val length: Int,
    val width: Int,
    val height: Int,
    val createdAt: Instant,
)
```

And we need to test the following method that clones an instance of `Box`, sorting its dimensions and keeping all other fields as is.
While the following test will detect any differences and clearly tell us which fields are different, it won't explain us why a field should have the expected value:

```kotlin
val originalBox = Box(
    barcode = "12345",
    label = "Misc. Stuff",
    length = 1,
    width = 2,
    height = 3,
    createdAt = Instant.MIN,
)

originalBox.withOrderedDimensions() shouldBeEqualUsingFields Box(
  barcode = "12345",
  label = "Misc. Stuff",
  length = 3,
  width = 2,
  height = 1,
  createdAt = Instant.MIN,
)
```

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter1Assertions/section1DataClasses/BoxTest0.kt)
<br/>
<br/>

Using one of the simplest testing styles, the `StringSpec`, we can clearly explain what we are doing:

```kotlin
    val originalBox = Box(
        barcode = "12345",
        label = "Misc. Stuff",
        length = 1,
        width = 2,
        height = 3,
        createdAt = Instant.MIN,
    )

    val actual = originalBox.withOrderedDimensions()

    init {
        "should sort dimensions" {
            assertSoftly(actual) {
                listOf(length, width, height) shouldContainExactlyInAnyOrder listOf(
                    originalBox.length,
                    originalBox.width,
                    originalBox.height,
                    )
                length shouldBeGreaterThanOrEqual width
                width shouldBeGreaterThanOrEqual height
            }
        }
        "should copy other fields as is" {
            assertSoftly(actual) {
                barcode shouldBe originalBox.barcode
                label shouldBe originalBox.label
                createdAt shouldBe originalBox.createdAt
            }
        }
    }
```

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter1Assertions/section1DataClasses/BoxTest1.kt)

<br/>
<br/>
Let's discuss the use of `assertSoftly` here. Without it, the first failed assertion aborts the test, and we wouldn't see the results of other assertions.
And it really helps to see the whole picture, not just an individual mismatch.
<br/>
<br/>
Let's have a look at another approach, using `withClue` to accomplish exactly the same thing:

```kotlin
    private val originalBox = Box(
        barcode = "12345",
        label = "Misc. Stuff",
        length = 1,
        width = 2,
        height = 3,
        createdAt = Instant.MIN,
    )

    private val actual = originalBox.withOrderedDimensions()

    init {
        "withOrderedDimensions works" {
            assertSoftly(actual) {
                withClue("dimensions match original ones with possibly different order") {
                    listOf(length, width, height) shouldContainExactlyInAnyOrder listOf(
                        originalBox.length,
                        originalBox.width,
                        originalBox.height,
                     )
                }
                withClue("dimensions are sorted") {
                    length shouldBeGreaterThanOrEqual width
                    width shouldBeGreaterThanOrEqual height
                }
                withClue("other fields are copied as is") {
                    barcode shouldBe originalBox.barcode
                    label shouldBe originalBox.label
                    createdAt shouldBe originalBox.createdAt
                }
            }
        }
    }
```

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter1Assertions/section1DataClasses/BoxTest2.kt)
<br/>
When a test wrapped in `withClue` fails, the error message of the failed assert is prefixed with the clue.
For instance, suppose we want to provide some explanation for the following assertion:
```kotlin
 2*2 shouldBe 5

Expected :5
Actual   :4
```
we can do it as follows:

```kotlin
withClue("Example from textbook on page 11") {
    2*2 shouldBe 5
}

Example from textbook on page 11
expected:<5> but was:<4>
Expected :5
Actual   :4
```
[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter1Assertions/section1DataClasses/ClueTest.kt)
<br/>
<br/>
The main point here in not to use `StringSpec` or `WordSpec` or any other style. 
The main point is to clearly explain why we are expecting exactly these values. 
Kotest provides multiple ways to do that - choose whatever works best for you.

## Using Fakery

If our dependency is a function, not an object, we don't need to mock - instead we can just build a test double.
Generally using test doubles instead of mocks makes our lives easier, especially when we are dealing with complex problems.
Usually we don't need any frameworks whatsoever to build test doubles - just plain simple functions built with Kotlin standard library will do.
Surely Kotest's fakery comes very handy in some more complex cases, but usually we don't need it.
<br/>
<br/>
We shall get to discussing complex scenarios later in this chapter, but let's start with a few simple ones.
Even in simple scenarios, using test doubles instead of mocks allows us to solve problems with less fuss.

### Basic Example - Replace A Mock with A Test Double

Suppose our class is named `DecisionsEngine` and it depends on another class named `AnsweringService`:

```kotlin
class DecisionsEngine(
    private val answeringService: AnsweringService,
)

class AnsweringService {
    fun answer(quuestion: String): Int {
        TODO()
    }
    // (snip)…
}
```
[The full code of AnsweringService can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/AnsweringService.kt)
[The full code of DecisionsEngine can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/DecisionsEngine.kt)

Naturally, in order to test `DecisionsEngine`, we need to mock `AnsweringService`, because our dependency is an object:

```kotlin
private val answeringService: AnsweringService = run {
    val ret = mockk<AnsweringService>()
    every { ret.answer(any()) } returns 42
    ret
}

private val decisionsEngine = DecisionsEngine(answeringService)
```
[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section1BasicExample/DecisionsEngineWithMockTest.kt)

Note, however, that even though `AnsweringService` has multiple methods, we can fully test `DecisionsEngine` while mocking only one of them - `answer`.
All other methods of `AnsweringService` are not used by `DecisionsEngine`, so it does not even need to know about them.
In fact, all that `DecisionsEngine` needs to know about is this: there is a function that takes a `String` question and returns an `Int` answer.
<br/>
<br/>
This is called loose coupling - `DecisionsEngine` only knows about its dependency what's needed for its own purposes.
So let's refactor `DecisionsEngine` to depend on a function instead of an object. 
Note that we don't need to change `AnsweringService` at all:

```kotlin
fun interface Answer {
    operator fun invoke(question: String): Int
}

// If we are wiring up dependencies manually, we can use this function:
// If we are using a DI framework, such as SpringBoot, that is discussed in the next example.
fun getDecisionsEngine(answeringService: AnsweringService): DecisionsEngineUsingFunction =
    DecisionsEngineUsingFunction(answeringService::answer)

class DecisionsEngineUsingFunction(
    private val answer: Answer,
) {
    fun decide(question: String): String {
        return """The decision on "$question" is ${answer(question)}"""
    }
}
```

[The full code of DecisionsEngineUsingFunction can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/DecisionsEngineUsingFunction.kt)
<br/>
<br/>
What does this refactoring buy us? Injecting a test double instead of a mock is way simpler:

```kotlin
private val serviceToTest = DecisionsEngineUsingFunction(
        answer = { 42 }
    )
```

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section1BasicExample/DecisionsEngineWithFunctionTest.kt)
<br/>
We don't need any mocking framework at all - just a simple lambda that returns one value.
Still, this is a relative small gain from this refactoring. We'll get to more significant benefits in more complex scenarios, later.
<br/>
<br/>
What about systems with DI frameworks, such as SpringBoot? Like in the previous example, we can refactor `DecisionsEngine` to depend on an interface instead of an object.
The following implementation is a bit more involved and it does require to modify `AnsweringService` as follows:

```kotlin
interface HasAnswer {
    fun answer(question: String): Int
}

// Typically this class would be annotated with @Service or another similar annotation
class AnsweringServiceV2 : HasAnswer {
    override fun answer(question: String): Int { 
(snip)
```

[The full code of AnsweringServiceV2 can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/AnsweringServiceV2.kt)

That done, `DecisionsEngine` can depend on `HasAnswer` interface instead of `AnsweringServiceV2` class - this is a concept understood and supported by SpringBoot:

```kotlin
// This class can be annotated with @Service or @Component or another similar annotation
class DecisionsEngineUsingInterface(
    private val hasAnswer: HasAnswer, // SpringBoot can inject this dependency
)
```

[The full code of DecisionsEngineUsingInterface can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/DecisionsEngineUsingInterface.kt)

And we can set up our test double in the test as follows:

```kotlin
    private val serviceToTest = DecisionsEngineUsingInterface(
        hasAnswer = object: HasAnswer { 
            override fun answer(question: String): Int = 42
        }
    )
```

Clearly this is more verbose than using a fun interface, and in this case this is no simpler than using a mocking framework.
So we should use test doubles with DI frameworks only in more complex scenarios, when using an interface instead of a class still brings significant benefits, as we shall discuss in the next examples.

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section1BasicExample/DecisionsEngineUsingInterfaceTest.kt)

Having discussed this most basic example, let's move on to slightly more involved ones.

### Example: Verifying That Test Double Was Not Called

### Example: Verifying That Test Double Was Called

## Learning Resources

- [Kotest Documentation](https://kotest.io/)
- [Kotest GitHub Repository](https://github.com/kotest/kotest)

## Contributing

Feel free to submit pull requests or create issues. 
[Contributing Guidelines](CONTRIBUTING.md)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
