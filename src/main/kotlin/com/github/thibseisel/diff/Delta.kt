package com.github.thibseisel.diff

import java.util.Objects

public sealed class Delta<T>(
    public val type: DeltaType,
    public val source: Chunk<T>,
    public val target: Chunk<T>
) {
    /**
     * Verify the chunk of this delta, to fit the target.
     */
    protected fun verifyChunk(target: List<T>) {
        source.verify(target)
    }

    public abstract fun applyTo(target: MutableList<T>)

    public abstract fun restore(target: MutableList<T>)

    /**
     * Create a new delta of the actual instance with customized chunk data.
     */
    public abstract fun withChunks(original: Chunk<T>, revised: Chunk<T>): Delta<T>

    override fun hashCode(): Int {
        return Objects.hash(source, target, type)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (other::class.java != this::class.java) {
            return false
        }
        other as Delta<*>
        if (!Objects.equals(source, other.source)) {
            return false
        }
        if (!Objects.equals(target, other.target)) {
            return false
        }
        return type == other.type
    }

    abstract override fun toString(): String
}

/**
 * @constructor Creates a change delta wih two given chunks.
 *
 * @param source The source chunk.
 * @param target The target chunk.
 */
public class ChangeDelta<T>(
    source: Chunk<T>,
    target: Chunk<T>
) : Delta<T>(DeltaType.CHANGE, source, target) {

    override fun applyTo(target: MutableList<T>) {
        verifyChunk(target)
        val position = source.position
        val size = source.size()
        for (i in 0 until size) {
            target.removeAt(position)
        }
        for ((i, line) in this.target.lines.withIndex()) {
            target.add(position + i, line)
        }
    }

    override fun restore(target: MutableList<T>) {
        val position = this.target.position
        val size = this.target.size()
        for (i in 0 until size) {
            target.removeAt(position)
        }
        for ((i, line) in this.source.lines.withIndex()) {
            target.add(position + i, line)
        }
    }

    override fun toString(): String =
        "[ChangeDelta, position: ${source.position}, lines: ${source.lines} to ${target.lines}]"

    override fun withChunks(original: Chunk<T>, revised: Chunk<T>): Delta<T> {
        return ChangeDelta(original, revised)
    }
}

public class InsertDelta<T>(
    original: Chunk<T>,
    revised: Chunk<T>
) : Delta<T>(DeltaType.INSERT, original, revised) {

    override fun applyTo(target: MutableList<T>) {
        verifyChunk(target)
        val position = this.source.position
        val lines = this.target.lines
        for (i in lines.indices) {
            target.add(position + i, lines[i])
        }
    }

    override fun restore(target: MutableList<T>) {
        val position = this.target.position
        val size = this.target.size()

        for (i in 0 until size) {
            target.removeAt(position)
        }
    }

    override fun toString(): String =
        "[InsertDelta, position: ${source.position}, lines: ${target.lines}]"

    override fun withChunks(original: Chunk<T>, revised: Chunk<T>): Delta<T> {
        return InsertDelta(original, revised)
    }

}

/**
 * Describes the delete-delta between original and revised texts.
 *
 * @param T The type of the compared elements in the "lines".
 */
public class DeleteDelta<T>(
    original: Chunk<T>,
    revised: Chunk<T>
) : Delta<T>(DeltaType.DELETE, original, revised) {

    override fun applyTo(target: MutableList<T>) {
        verifyChunk(target)
        val position = this.source.position
        val size = this.source.size()
        for (i in 0 until size) {
            target.removeAt(position)
        }

    }

    override fun restore(target: MutableList<T>) {
        verifyChunk(target)
        val position = this.target.position
        val lines = this.source.lines
        for ((i, line) in lines.withIndex()) {
            target.add(position + i, line)
        }
    }

    override fun toString(): String =
        "[DeleteDelta, position: ${source.position}, lines: ${source.lines}]"

    override fun withChunks(original: Chunk<T>, revised: Chunk<T>): Delta<T> {
        return DeleteDelta(original, revised)
    }

}

