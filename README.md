# Kotest Cookbook

<!-- TOC -->
* [Assertions](#Assertions)
  * [Data Classes](#matching-data-classes)
  * [Json](#json)
  * [Collections](#collections)
<!-- TOC -->

## Assertions

There are multiple ways to accomplish most common tasks in Kotest. So let's discuss how they compare, what are their pros and cons, and what trade-offs we should consider when choosing one over another.

### Matching Data Classes

The ubiquitous `shouldBe` does detect the difference between two objects:

```kotlin
largeRedSweetApple shouldBe largeRedTartApple
```

and the output can be easy to grok in the IDE, but less so in CI logs:

```
data class diff for io.kotest.cookbook.chapter1Assertions.Fruit
└ taste:
Expected :Fruit(name=Apple, color=Red, size=Large, taste=Tart)
Actual   :Fruit(name=Apple, color=Red, size=Large, taste=Sweet)
```

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

So `shouldBeEqualUsingFields` exposes differences in a very readable way. But it does not allow to explain why we expect these values. As such, it is a great choice in situations where such explanations are not needed, su as:

* deserialize a message correctly
* correctly map data from one layer to another
* read correct date from the database

It's also a good choice when we need to move quickly and are not overly concerned about long-term maintainability of the tests.
<br/>
<br/>
We can also customize `shouldBeEqualUsingFields` to provide non-default matchers for some fields or to ignore them altogether.
For instance, the following code ignores the timestamp field when comparing two objects:

```kotlin

```

```kotlin
## Learning Resources

- [Kotest Documentation](https://kotest.io/)
- [Kotest GitHub Repository](https://github.com/kotest/kotest)

## Contributing

Feel free to submit pull requests or create issues.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
