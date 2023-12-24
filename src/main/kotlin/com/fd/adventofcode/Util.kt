package com.fd.adventofcode

import java.io.File
import java.util.*
import java.util.regex.Pattern

data class Point(val x: Long, val y: Long)
fun Scanner.findAllInt() = asSequence().map { it.toInt() }.toList()
fun Scanner.skipAndSet(s: String) = skip(delimiter()).useDelimiter(s)
fun Scanner.skipAndSet(p: Pattern) = skip(delimiter()).useDelimiter(p)

fun writeFile(name: String, contents: String) = File(name).writeText(contents)

operator fun Pair<Int, Int>.plus(rhs: Pair<Int, Int>) = first + rhs.first to second + rhs.second
operator fun Pair<Int, Int>.minus(rhs: Pair<Int, Int>) = first - rhs.first to second - rhs.second
operator fun IntRange.plus(i: Int) = first+i..last+i

fun Any?.println() = println(this)

fun List<String>.column(i: Int) = map { it[i] }.joinToString("")
fun List<String>.transposed() = firstOrNull()?.indices?.map { column(it) } ?: emptyList()
fun List<String>.hasIndices(x: Int, y: Int) = y in indices && x in first().indices

fun List<String>.toMatrix() = map { it.map { it }.toMutableList() }

fun List<MutableList<Char>>.toStrings() = map { it.joinToString("") }

fun Int.toCircled() = when (this) {
    0 -> '⓪'
    in 1..20 -> ('①'.code + this - 1).toChar()
    else -> throw IllegalArgumentException("toCircled: must be 0..20")
}

fun Int.toCircledStrong() = when (this) {
    0 -> '⓿'
    in 1..10 -> ('⓵'.code + this - 1).toChar()
    in 11..20 -> ('⓫'.code + this - 11).toChar()
    else -> throw IllegalArgumentException("toCircledStrong: must be 0..20")
}

fun Int.toSuperscript() = when (this) { 0->'⁰';1->'¹';2->'²';3->'³';4->'⁴';5->'⁵';6->'⁶';7->'⁷';8->'⁸';9->'⁹';else->'X' }
fun Int.toSubscript() = when (this) { 0->'₀';1->'₁';2->'₂';3->'₃';4->'₄';5->'₅';6->'₆';7->'₇';8->'₈';9->'₉';else->'X' }

fun iota(start: Int = 0) = generateSequence(start) { it + 1 }