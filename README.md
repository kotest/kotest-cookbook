# Kotest Cookbook

<!-- TOC -->
* [Assertions](#Assertions)
  * [Data Classes and shouldBeEqualUsingFields](#matching-data-classes-with-shouldbeequalusingfields)
  * [Explicitly Matching Fields of Data Classes](#explicitly-matching-fields-of-data-classes)
  * [Json](#json)
  * [Collections](#collections)
<!-- TOC -->

## Assertions

There are multiple ways to accomplish most common tasks in Kotest. So let's discuss how they compare, what are their pros and cons, and what trade-offs we should consider when choosing one over another.
<br/>
<br/>
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


The main point here in not to use `StringSpec` or `WordSpec` or any other style. 
The main point is to clearly explain why we are expecting exactly these values. 
Kotest provides multiple ways to do that - choose whatever works best for you.



## Learning Resources

- [Kotest Documentation](https://kotest.io/)
- [Kotest GitHub Repository](https://github.com/kotest/kotest)

## Contributing

Feel free to submit pull requests or create issues.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
