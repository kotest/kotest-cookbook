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
  * [Using Test Doubles And Fakery](#using-test-doubles-and-fakery)
    * [Basic Example - Replace A Mock with A Test Double](#basic-example---replace-a-mock-with-a-test-double)
    * [Verifying That Test Double Was Not Called](#verifying-that-test-double-was-not-called)
    * [Basic Example: Verifying That Test Double Was Called](#basic-example-verifying-that-test-double-was-called)
    * [Verifying That Test Double Was Called](#verifying-that-test-double-was-called)
    * [Test Double Returning Different Values on Subsequent Calls With Fakery](#test-double-returning-different-values-on-subsequent-calls-with-fakery)
    * [Using Fakery To Cancel A Long-Running Loop](#using-fakery-to-cancel-a-long-running-loop)
    * [Using Fakery To Test Rate Limiter](#using-fakery-to-test-rate-limiter)
    * [Test Doubles Are Cool, But Don't Overdo It](#test-doubles-are-cool-but-dont-overdo-it)
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
we can use `shouldBeEqualUsingFields` assertion, as follows:

```kotlin
largeRedSweetApple shouldBeEqualUsingFields largeRedTartApple
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
* read correct data from the database

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
For instance, the following code ignores the `createdAt` field when comparing two objects:

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
There are multiple ways to match data classes - it might be easier to explicitly match the fields we want using the matchers of our choice.
<br/>
<br/>
In the next few examples we shall do that.

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
And it really helps to see the whole picture, not only the first individual mismatch.
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
The main point here is not to use `StringSpec` or `WordSpec` or any other style. 
The main point is to clearly explain why we are expecting exactly these values. 
Kotest provides multiple ways to do that - choose whatever works best for you.

## Using Test Doubles And Fakery

If our dependency is a function, not an object, we don't need to mock - instead we can build a test double.
Generally using test doubles instead of mocks makes our lives easier, especially when we are dealing with complex problems.
Usually we don't need any frameworks whatsoever to build test doubles - plain simple functions built with Kotlin standard library will do.
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
    fun answer(question: String): Int {
        TODO()
    }
    // (snip)…
}
```
[The full code of AnsweringService can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/AnsweringService.kt)
<br/>
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
What does this refactoring buy us? Injecting a test double instead of a mock is simpler:

```kotlin
private val serviceToTest = DecisionsEngineUsingFunction(
        answer = { 42 }
    )
```

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section1BasicExample/DecisionsEngineWithFunctionTest.kt)
<br/>
We don't need any mocking framework at all - a simple lambda that returns one value will do.
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

[The full code of AnsweringServiceV2 can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/AnsweringServiceWithInterface.kt)

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
So we should use test doubles with DI frameworks only in more complex scenarios, when using a test double instead of a mock still brings significant benefits, as we shall discuss in the next examples.

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section1BasicExample/DecisionsEngineUsingInterfaceTest.kt)

Having discussed this most basic example, let's move on to slightly more involved ones.

### Verifying That Test Double Was Not Called

The simplest way to verify that a test double was not called is to add a failed assertion right inside the test double.
Suppose, for example, that we need to verify that a decision was made without alerting.
The following test double for `alert` does that:
```kotlin
val serviceToTest = DecisionsEngineWithAlerting(
    answer = { 42 },
    alert = { severity: AlertSeverity, message: String ->
        failSoftly("Alert was called with severity $severity and message: $message")
    }
)
```

If we want to get complete information about all calls made to the test double, we can use a mutable list to record them:

```kotlin
val alertingCalls = mutableListOf<Pair<AlertSeverity, String>>()
val serviceToTest = DecisionsEngineWithAlerting(
    answer = { 42 },
    alert = { severity: AlertSeverity, message: String ->
        alertingCalls.add(Pair(severity, message))
    }
)
```

That done, we can utilize the full power of all Kotest's assertion to analyze the recorded calls.
In this basic example, we don't really need anything complicated, one short assertion will do:

```kotlin
alertingCalls.shouldBeEmpty()
```

More advanced examples will follow soon.

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section2VerifyNotCalled/VerifyingTestDoubleWasNotCalledTest.kt)

As we have seen, we don't need any frameworks to build test doubles and verify that they were not called.

### Basic Example: Verifying That Test Double Was Called

The simplest way to make sure that a test double was called is to increment a counter inside the test double.
While we are at it, we can also assert that the parameters passed to the test double are as expected:

```kotlin
var callCount = 0
val systemToTest = DecisionsEngineUsingFunction(
    answer = { question : String ->
        question.shouldNotContain("apple")
        callCount++
        42
    }
)
systemToTest.decide("Do oranges taste better than bananas?")
callCount shouldBe 1
```

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section3VerifyCalled/VerifyingTestDoubleWasCalledTest.kt)

If this assertion fails, our test stops right there. We might not want that - quite often we want to see the whole picture rather than the first failure.
The next example shows how to do that.


### Verifying That Test Double Was Called

Suppose that we are testing an object's method that accepts a `List` and does the following:

* split the list into chunks
* pass each chunk to a dependency for processing
* both the order of chunks and the order of elements in each chunk do not matter

as is shown in the following code snippet:

```kotlin
interface ContainerProcessor {
    fun process(container: Container)
}

class ElementsProcessorWithObjectDependency(
  private val containerProcessor: ContainerProcessor,
  private val maxChunkSize: Int,
) {
    fun process(elements: List<Int>) 
  (snip)...

  data class Container(
    val elements: List<Int>
  )
```

[The full code of ContainerFactoryWithObject can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/ElementsProcessor.kt)

Traditionally, we would mock the dependency and verify that it was called with expected chunks, as follows:

```kotlin
private val containerProcessor = run {
    val ret = mockk<ContainerPrinter>()
    justRun { ret.process(any()) }
    ret
}

val factory = ContainerFactoryWithObject(
    containerProcessor,
)
factory.process(listOf(1, 2, 3, 4, 5))
// Here we are verifying how the factory is implemented,
// not that it meets the requirements.
verify(exactly = 1) { containerProcessor.process(Container(listOf(1, 2))) }
verify(exactly = 1) { containerProcessor.process(Container(listOf(3, 4))) }
verify(exactly = 1) { containerProcessor.process(Container(listOf(5))) }
```

In this test we are not verifying that the requirements are met - we are verifying how the factory is implemented.
And this approach has the following two drawbacks:

* If the implementation of `process` changes, the test will break even if the requirements are still met.
* The test does not explain what exactly we expect from the output.

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section3VerifyCalled/ElementsProcessorTestWithMocks.kt)

This is one of those cases where test doubles shine - we can capture the calls made to the test double and store them in a list, which requires very little learning and is straightforward to do.
That done, we can use the full power of Kotlin standard library as well as Kotest's assertions to explain what are the requirements,
and to assert exactly that they are met,
without caring about the implementation details.
Let's see how straightforward it is:

```kotlin
val calls = mutableListOf<Container>()
val serviceToTest = ElementsProcessorWithFunctionDependency(
    processContainer = { container: Container ->
        calls.add(container)
    },
    maxChunkSize = 2,
)
val elementsToProcess = listOf(1, 2, 3, 4, 5)
serviceToTest.process(elementsToProcess)
// withClue allows us to explain the requirements
withClue("each element is in exactly one container") {
    val processedElements = calls.flatMap { it.elements }
    processedElements shouldContainExactlyInAnyOrder elementsToProcess
}
withClue("elements are correctly chunked") {
    calls.forAll { container ->
        // real life requirements could be more complex
        // we need to keep them simple in this example
        container.elements.size shouldBeIn 1..2
    }
}
```

Should our implementation of `process` change, this test will still pass as long as the requirements are met.
For instance, if the `process` method after the change provides the following chunks: `[2, 5], [1, 4], [3]`, the test will still pass.
<br/>
<br/>
So far we have been able to get by without any frameworks at all - only plain Kotlin code and Kotest assertions.
Now let's see how Kotest's fakery can help us in more complex scenarios. It's only two simple functions, 

### Test Double Returning Different Values on Subsequent Calls With Fakery

Mocking libraries such as `Mockk` have a really handy feature - the ability to return different values on subsequent calls, such as:

```kotlin
every { service.answer(any()) } returns 42 andThen 43 andThen 44
```

While we generally don't need any mocking library in functional programming, this feature is extremely useful in some cases.
This is the only case where we need Kotest's fakery, which has only two simple functions. 
If our test double never needs to throw exceptions, we can use the following extension function:

```kotlin
// toFunction is an extension function in Kotest's fakery
val answers = sequenceOf(42, 43, 44).toFunction()
answers.next() shouldBe 42
answers.next() shouldBe 43
answers.next() shouldBe 44
// Inject this test double as a dependency as follows:
val decisionsEngine = DecisionsEngineUsingFunction(
    answer = { answers.next() }
)
```

Should we need the test double to throw exceptions on some calls, we can do it as follows:

```kotlin
val answers = sequenceOf(
    Result.success(42),
    Result.failure(Exception("Oops!")),
    Result.success(44),
).toFunction()
answers.next() shouldBe 42
shouldThrow<Exception> { answers.next() }.message shouldBe "Oops!"
answers.next() shouldBe 44
```

It is important that we are using an extension function on a `Sequence` and not on a `List`. 
The reason is simple - sequences are evaluated lazily, so we can invoke any side effects along with providing the values.
The following example shows how that works:

```kotlin
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
```

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section3VerifyCalled/ReturningMultipleValuesTest.kt)

Let's discuss a real life example where this feature is really useful.

### Using Fakery To Cancel A Long-Running Loop

Suppose that we have a process that executes tasks in a loop, and that process can be cancelled mid-flight and needs to stop as soon as the current task has completed.
The following code shows the implementation, which is very simple:

```kotlin
class CancellableTaskProcessor(
    private val processTask: ProcessTask,
) {
    private val isCancelledRef = AtomicBoolean(false)

  fun processTasks(tasks: Sequence<String>) = tasks
    .takeWhile { !isCancelledRef.get() }
    .map { processTask(it) }
    .toList()
    
    fun cancel() = isCancelledRef.set(true)
}
```

While the implementation is simple, writing tests for it requires some orchestration, a lot of work, and the result is less than perfect.
To test that the loop exits as soon as we've invoked `cancel`, we would need to run something in parallel.
It could be done with threads or coroutines, but either way we would be doing something like this:

| Thread 1               | Thread 2               |
|------------------------|------------------------|
| Start processing tasks |                        |
| Process task 1         |                        |
|                        |       Cancel           |

And then we would verify which tasks were processed.
<br/>
<br/>
While this is clearly doable, it requires a lot of work, and the test may be:

* imprecise - we might not be able to guarantee exactly when the `cancel` call happens, so we cannot expect exactly how many tasks were processed
* flaky - even though we do not match number of processed tasks exactly, sometimes the test may fail.
  <br/>
  <br/>
Using fakery, we can easily make our test both precise and non-flaky - it will always run with exactly the same outcome.
Let's see how straightforward it is:

```kotlin
    private val results: PlaybackElements<String> = sequence<String> {
        // the code below starts executing after `takeWhile` evaluates to true for the first time
        processor.cancel()
        yield("result1")
        // after `cancel` is called, `takeWhile` will evaluate to false
        // so the second yield will never be reached
        yield("result2")
    }.toFunction()

private val processedTasks = mutableListOf<String>()

private val processor = CancellableTaskProcessor(
    processTask = { task -> processedTasks.add(task) }
)

init {
    "stops processing tasks when cancelled" {
        processor.processTasks(tasks)
        processedTasks shouldBe listOf("task1")
    }
}
```

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section4ExampleCancel/CancelLongRunningLoopTest.kt)

### Using Fakery To Test Rate Limiter

Suppose that our system calls some external service, and we must never exceed the rate we are calling that service.
In this example we shall be testing such a rate limiter, using test doubles and Kotest's fakery.
Suppose the rate limiter is implemented as follows:

```kotlin
requests.forEach { request ->
    val startedAt = Instant.now()
    externalServiceCall(request)
    val endedAt = Instant.now()
    val duration = endedAt.toEpochMilli() - startedAt.toEpochMilli()
    if(duration < allowedFrequencyInMilliseconds) {
        val sleepTime = allowedFrequencyInMilliseconds - duration
        delay(sleepTime)
    }
}
```

[The full code of RateLimiter can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/RateLimiter.kt)

While this implementation is simple, testing it is not so easy. 
We definitely can record the times when the external service started and stopped processing each task,
and from that we can estimate how long the rate limiter delayed between calls.
Our estimates, however, will be imprecise, and building such a test will require some work.
Testing this code with test doubles is much easier.
<br/>
<br/>
Let us replace hardcoded calls to `Instant.now()` and `delay()` with injectable dependencies - that will allow us to know exactly for how long we delay between calls to external service. 
The following code is very easy to test with test doubles:

```kotlin
fun interface ExternalServiceCall {
    operator fun invoke(request: String)
}

fun interface GetNow {
    operator fun invoke(): Instant
}

fun interface DelayFor {
    suspend operator fun invoke(milliseconds: Long)
}

class RateLimiter(
  private val externalServiceCall: ExternalServiceCall,
  private val allowedFrequencyInMilliseconds: Int,
  private val getNow: GetNow,
  private val delayFor: DelayFor,
) {
  suspend fun callService(requests: Sequence<String>) {
    requests.forEach { request ->
      val startedAt = getNow()
      externalServiceCall(request)
      val endedAt = getNow()
      val duration = endedAt.toEpochMilli() - startedAt.toEpochMilli()
      if(duration < allowedFrequencyInMilliseconds) {
        val sleepTime = allowedFrequencyInMilliseconds - duration
        delayFor(sleepTime)
      }
    }
  }
}
```

[The full code of RateLimiter with dependencies can be found here](src/main/kotlin/io/kotest/cookbook/chapter2Fakery/RateLimiter.kt)

The test for `RateLimiter` is straightforward and precise - it will always run with exactly the same outcome:

```kotlin
val externalServiceCalls = mutableListOf<String>()
val instances = sequenceOf(100L, 150L, 200L, 210L)
    .map { Instant.ofEpochMilli(it) }
    .toFunction()
val delays = mutableListOf<Long>()
val rateLimiter = RateLimiter(
    externalServiceCall = { request ->
        externalServiceCalls.add(request)
    },
    allowedFrequencyInMilliseconds = 100,
    getNow = { instances.next() },
    delayFor = { delayForMillis ->
        delays.add(delayForMillis)
    }
)
rateLimiter.callService(
    sequenceOf("task1", "task2")
)
withClue("should process all tasks in order") {
    externalServiceCalls shouldContainExactly listOf("task1", "task2")
}
withClue("should have correct delays") {
    delays shouldContainExactly listOf(50L, 90L)
}
```

[The full example can be found here](src/test/kotlin/io/kotest/cookbook/chapter2Fakery/section5RateLimiter/RateLimiterTest.kt)
<br/>
<br/>
Of course, there are multiple other ways to replace actual time with mock values.
We shall discuss it more in the chapter about flaky tests.
With test doubles, however, we can build precise and non-flaky tests with very little effort.
And we shall not inadvertently break other tests while mocking static functions such as `Instant.now()`.

### Test Doubles Are Cool, But Don't Overdo It

No tool is the best fit for all purposes. Test doubles are no exception.
For example, when we replace calls to `Instant.now()` and `delay()` with dependencies, 
we end up completely disconnected from the reality. 
<br/>
<br/>
If we have any bugs, using real time might sometimes expose them.
For instance, we can have code that usually works, but fails exactly on an hour boundary, or on February 29th, or during the week when daylight saving time changes.
Once we have replaced real time with test doubles, that possibility is gone, we won't catch those bugs anymore.
We shall discuss it more in the chapter about flaky tests.
<br/>
<br/>
The gist of this chapter was to show use cases when test doubles really shine, not to discourage using mocks altogether.
Mocking libraries such as `mockk` are massively powerful and useful, but we are suggesting to complement them with test doubles in functional programming, especially in more difficult situations.

## Flaky Tests

We'll discuss what Kotest has to offer to deal with flaky tests.

### Using Eventually

Whenever a test intermittenyly fails, it's common to just wrap it in `eventually`, as follows:

```kotlin
eventually(5.seconds) {
  runTestThatIntermittenlyFails()
}
```

And in some cases this is the right thing to do. For instance, if we are running an integration test as follows:

* kafka is running in a Docker container
* we publish a message
* we test that the message has been consumed correctly

In this situation we have no control over how long will it take for the message to get consumed. Eventually, it should happen, so we shall keep running our test until it succeeds:

```kotlin
publishTestMessage()
eventually(5.seconds) {
  verifyThatMessageConsumedCorrectly()
}
```

In this case `eventually` is a perfect tool for the task at hand. It will keep trying until success, but not any longer than needed.
<br/>
<br/>
However, every time we are wrapping a flaky test in `eventually` we need to understand why the test is flaky. And in many cases we should consider other alternatives.

## Learning Resources

- [Kotest Documentation](https://kotest.io/)
- [Kotest GitHub Repository](https://github.com/kotest/kotest)

## Contributing

Feel free to submit pull requests or create issues. 
[Contributing Guidelines](CONTRIBUTING.md)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
