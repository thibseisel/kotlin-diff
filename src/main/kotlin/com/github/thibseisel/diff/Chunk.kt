package com.github.thibseisel.diff

import java.util.Objects

/**
 * Holds the information about the part of text involved in the diff process.
 *
 * Text is represented as `Array<Any?>` because the diff engine is capable of handling more than plain ascii.
 * In fact, arrays or lists of any type that implements [Any.hashCode] and [Any.equals] correctly
 * can be subject to differencing using this library.
 *
 * @param T The type of the compared elements in the "lines".
 *
 * @constructor
 * Creates a chunk and saves a copy of affected lines.
 *
 * @param position The start position
 * @param lines the affected lines
 * @param changePosition the positions of changes lines
 */
public class Chunk<T>
@JvmOverloads public constructor(
    public val position: Int,
    lines: List<T>,
    private val changePosition: List<Int>? = null
) {
    public var lines: List<T> = lines.toList()

    /**
     * Verifies that this chunk's saved text matches the corresponding text in the given sequence.
     *
     * @param target the sequence to verify against.
     */
    public fun verify(target: List<T>) {
        if (position > target.size || last() > target.size) {
            error("Incorrect Chunk: the position of chunk > target size")
        }

        for (i in 0 until size()) {
            if (target[position + i] != lines[i]) {
                error("Incorrect Chunk: the chunk content doesn't match the target")
            }
        }
    }

    public fun size(): Int = lines.size

    public fun last(): Int = position + size() - 1

    override fun hashCode(): Int {
        return Objects.hash(lines, position, size())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (other !is Chunk<*>) {
            return false
        }

        return position == other.position
    }

    override fun toString(): String {
        return "[position: $position, size: ${size()}, lines: $lines]"
    }
}
