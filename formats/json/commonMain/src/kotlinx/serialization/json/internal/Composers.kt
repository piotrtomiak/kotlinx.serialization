/*
 * Copyright 2017-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalSerializationApi::class)
package kotlinx.serialization.json.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlin.jvm.JvmField

internal fun Composer(sb: JsonWriter, json: Json): Composer =
    if (json.configuration.prettyPrint) ComposerWithPrettyPrint(sb, json) else Composer(sb)

@OptIn(ExperimentalSerializationApi::class)
internal open class Composer(@JvmField internal val writer: JsonWriter) {
    var writingFirst = true
        protected set

    open fun indent() {
        writingFirst = true
    }

    open fun unIndent() = Unit

    open fun nextItem() {
        writingFirst = false
    }

    open fun space() = Unit

    fun print(v: Char) = writer.writeChar(v)
    fun print(v: String) = writer.write(v)
    open fun print(v: Float) = writer.write(v.toString())
    open fun print(v: Double) = writer.write(v.toString())
    open fun print(v: Byte) = writer.writeLong(v.toLong())
    open fun print(v: Short) = writer.writeLong(v.toLong())
    open fun print(v: Int) = writer.writeLong(v.toLong())
    open fun print(v: Long) = writer.writeLong(v)
    open fun print(v: Boolean) = writer.write(v.toString())
    fun printQuoted(value: String) = writer.writeQuoted(value)
}

@ExperimentalUnsignedTypes
internal class ComposerForUnsignedNumbers(writer: JsonWriter) : Composer(writer) {
    override fun print(v: Int) {
        return super.print(v.toUInt().toString())
    }

    override fun print(v: Long) {
        return super.print(v.toULong().toString())
    }

    override fun print(v: Byte) {
        return super.print(v.toUByte().toString())
    }

    override fun print(v: Short) {
        return super.print(v.toUShort().toString())
    }
}

internal class ComposerWithPrettyPrint(
    writer: JsonWriter,
    private val json: Json
) : Composer(writer) {
    private var level = 0

    override fun indent() {
        writingFirst = true
        level++
    }

    override fun unIndent() {
        level--
    }

    override fun nextItem() {
        writingFirst = false
        print("\n")
        repeat(level) { print(json.configuration.prettyPrintIndent) }
    }

    override fun space() {
        print(' ')
    }
}
