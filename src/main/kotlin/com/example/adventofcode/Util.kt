package com.fd.adventofcode

import java.io.File
import java.util.*
import java.util.regex.Pattern

open class AdventBase(private val year: Int) {
    private fun getFile(day: Int): File {
        val infile = File("res/$year/${"$day".padStart(2, '0')}.txt")
        assert(infile.isFile) { "File does not exist: ${infile.absolutePath}" }
        return infile
    }
    fun getInput(day: Int): List<String> = getFile(day).readLines()

    fun getString(day: Int): String = getFile(day).readText()

    fun Scanner.findAllInt() = asSequence().map { it.toInt() }.toList()
    fun Scanner.skipAndSet(s: String) = skip(delimiter()).useDelimiter(s)
    fun Scanner.skipAndSet(p: Pattern) = skip(delimiter()).useDelimiter(p)

    fun writeFile(name: String, contents: String) = File(name).writeText(contents)

    operator fun Pair<Int, Int>.plus(rhs: Pair<Int, Int>) = first + rhs.first to second + rhs.second
    operator fun Pair<Int, Int>.minus(rhs: Pair<Int, Int>) = first - rhs.first to second - rhs.second
    operator fun IntRange.plus(i: Int) = first+i..last+i

    fun Any?.println() = println(this)
}