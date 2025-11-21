package io.kotest.cookbook.chapter2Fakery

interface ContainerProcessor {
    fun process(container: Container)
}

class ContainerElementsPrinter: ContainerProcessor {
    override fun process(container: Container) = println(container.elements)
}

class ContainerFactoryWithObject(
    private val containerProcessor: ContainerProcessor,
    private val maxSize: Int,
) {
    fun processInChunks(elements: List<Int>) = elements
        .chunked(maxSize)
        .forEach {
            containerProcessor.process(Container(it))
        }
}

fun interface ProcessContainer {
    operator fun invoke(container: Container)
}

class ContainerFactoryWithFunction(
    private val processContainer: ProcessContainer,
    private val maxSize: Int,
) {
    fun processInChunks(elements: List<Int>) = elements
        .chunked(maxSize)
        .forEach {
            processContainer(Container(it))
        }
}

data class Container(
    val elements: List<Int>
)