package com.github.thibseisel.diff

import java.util.Objects

public sealed class Delta<T>(
    public val type: DeltaType,
    public val source: Chunk<T>,
    public val target: Chunk<T>
) {
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

    override fun toString(): String =
        "[ChangeDelta, position: ${source.position}, lines: ${source.lines} to ${target.lines}]"
}

public class InsertDelta<T>(
    original: Chunk<T>,
    revised: Chunk<T>
) : Delta<T>(DeltaType.INSERT, original, revised) {

    override fun toString(): String =
        "[InsertDelta, position: ${source.position}, lines: ${target.lines}]"
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

    override fun toString(): String =
        "[DeleteDelta, position: ${source.position}, lines: ${source.lines}]"
}
