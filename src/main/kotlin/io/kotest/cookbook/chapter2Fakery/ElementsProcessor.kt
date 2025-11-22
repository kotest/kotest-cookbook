package io.kotest.cookbook.chapter2Fakery

interface ContainerProcessor {
    fun process(container: Container)
}

class ContainerPrinter: ContainerProcessor {
    override fun process(container: Container) = println(container.elements)
}

class ElementsProcessorWithObjectDependency(
    private val containerProcessor: ContainerProcessor,
    private val maxChunkSize: Int,
) {
    // In real life dividing elements into chunks may be quite complicated,
    // considering elements' properties such as size, weight, if it's fragile, perishable, contains hazmat etc.
    // To keep the example simple we just chunk by a fixed max size.
    fun process(elements: List<Int>) = elements
        .chunked(maxChunkSize)
        .forEach {
            containerProcessor.process(Container(it))
        }
}

fun interface ProcessContainer {
    operator fun invoke(container: Container)
}

class ElementsProcessorWithFunctionDependency(
    private val processContainer: ProcessContainer,
    private val maxChunkSize: Int,
) {
    fun process(elements: List<Int>) = elements
        .chunked(maxChunkSize)
        .forEach {
            processContainer(Container(it))
        }
}

data class Container(
    val elements: List<Int>
)