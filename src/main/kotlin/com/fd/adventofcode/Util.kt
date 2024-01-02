package com.fd.adventofcode

import java.io.File
import java.security.MessageDigest
import java.util.*
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

data class Point(val x: Long, val y: Long)
fun Scanner.findAllInt() = asSequence().map { it.toInt() }.toList()
fun Scanner.skipAndSet(s: String) = skip(delimiter()).useDelimiter(s)
fun Scanner.skipAndSet(p: Pattern) = skip(delimiter()).useDelimiter(p)

fun writeFile(name: String, contents: String) = File(name).writeText(contents)
fun appendFile(name: String, contents: String) = File(name).appendText(contents)
fun ByteArray.toHex() = joinToString("") { byte -> "%02x".format(byte) }
fun md5(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(Charsets.UTF_8))

operator fun Pair<Int, Int>.plus(rhs: Pair<Int, Int>) = first + rhs.first to second + rhs.second
operator fun Pair<Int, Int>.minus(rhs: Pair<Int, Int>) = first - rhs.first to second - rhs.second
operator fun IntRange.plus(i: Int) = first+i..last+i
infix fun <A, B, C> Pair<A, B>.to3(that: C): Triple<A, B, C> = Triple(first, second, that)

fun Any?.println() = println(this)

fun Char.isHexDigit() = isDigit() || (code >= 'a'.code && code <= 'f'.code) || (code >= 'A'.code && code <= 'F'.code)

fun List<String>.column(i: Int) = map { it[i] }.joinToString("")
fun List<String>.transposed() = firstOrNull()?.indices?.map { column(it) } ?: emptyList()
fun List<String>.hasIndices(x: Int, y: Int) = y in indices && x in first().indices
fun List<String>.printBoxed() {
    println("┏${"━".repeat(firstOrNull()?.length ?: 0)}┓")
    forEach { println("┃$it┃") }
    println("┗${"━".repeat(lastOrNull()?.length ?: 0)}┛")
}

fun List<String>.toMatrix() = map { it.map { it }.toMutableList() }
fun List<List<Char>>.toStrings() = map { it.joinToString("") }
fun <E : Any?> List<E>.deepcopy(): List<E> = map { if (it is List<*>) (it.deepcopy() as E) else it }


fun <E : Any?> List<List<E>>.column(i: Int) = map { it[i] }.toMutableList()
fun <E : Any?> List<MutableList<E>>.setColumn(i: Int, col: List<E>) = col.forEachIndexed { r, v -> this[r][i] = v }
@JvmName("transposedList")
fun <E : Any?> List<List<E>>.transposed() = firstOrNull()?.indices?.map { column(it) } ?: emptyList()
fun <E : Any?> List<List<E>>.rotatedLeft() = transposed().reversed()
fun <E : Any?> List<List<E>>.rotatedRight() = reversed().transposed()
fun <E : Any?> List<List<E>>.rotated180() = rotatedLeft().rotatedLeft()

fun <T> Sequence<T>.repeat(prefix: Sequence<T> = emptySequence()) = sequence { yieldAll(prefix) ; while (true) yieldAll(this@repeat) }

fun <T> zipAll(seq: Collection<Sequence<T>>): Sequence<List<T>> {
    return sequence {
        val its = seq.map { it.iterator() }
        while (its.all { it.hasNext() }) yield(its.map { it.next() })
    }
}
fun <T> zipAll(vararg seq: Sequence<T>): Sequence<List<T>> = zipAll(seq.toList())

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

fun Int.toSuperscript() = when (this) { 0->'⁰';1->'¹';2->'²';3->'³';4->'⁴';5->'⁵';6->'⁶';7->'⁷';8->'⁸';9->'⁹';else->throw IllegalArgumentException() }
fun Int.toSubscript() = when (this) { 0->'₀';1->'₁';2->'₂';3->'₃';4->'₄';5->'₅';6->'₆';7->'₇';8->'₈';9->'₉';else->throw IllegalArgumentException() }

fun iota(start: Int = 0) = generateSequence(start) { it + 1 }
fun Int.modulo(divisor: Int) = if (this >= 0) this % divisor else (divisor - (-this % divisor)) % divisor
fun Long.modulo(divisor: Long) = if (this >= 0) this % divisor else (divisor - (-this % divisor)) % divisor
fun lcm(a: Long, b: Long): Long {
    val larger = max(a, b)
    val smaller = min(a, b)
    val maxLcm = a * b
    return LongProgression.fromClosedRange(larger, maxLcm, larger).find { it % smaller == 0L }!!
}
fun lcm(nums: List<Long>) = nums.reduce { acc, n -> lcm(acc, n) }