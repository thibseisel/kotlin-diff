package com.github.thibseisel.diff.myers

internal class PathNode(
    /**
     * Position in the original sequence.
     */
    val i: Int,

    /**
     * Position in the revised sequence.
     */
    val j: Int,

    val snake: Boolean,

    /**
     * Whether this is a bootstrap node.
     * Bootstrap nodes have one of their two coordinates less than zero.
     */
    val bootstrap: Boolean,

    /**
     * The previous node in the path.
     */
    val prev: PathNode?
) {
    /**
     * Skips sequences of [PathNode]s until a snake or bootstrap node is found, or the end of the path is reached.
     * @return The next first [PathNode] or bootstrap node in the path, or `null` if none found.
     */
    fun previousSnake(): PathNode? = when {
        bootstrap -> null
        !snake && prev != null -> prev.previousSnake()
        else -> this
    }

    override fun toString(): String = buildString {
        append('[')
        var node: PathNode? = this@PathNode
        while (node != null) {
            append('(')
            append(node.i)
            append(',')
            append(node.j)
            append(')')
            node = node.prev
        }
        append(']')
    }
}
