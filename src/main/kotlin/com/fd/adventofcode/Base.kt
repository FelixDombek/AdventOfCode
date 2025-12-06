package com.fd.adventofcode

import java.io.File

open class AdventBase(private val year: Int) {
    private fun getFile(day: Int): File {
        val infile = File("res/$year/${"$day".padStart(2, '0')}.txt")
        assert(infile.isFile) { "File does not exist: ${infile.absolutePath}" }
        this.day = day
        return infile
    }
    fun getInput(day: Int): List<String> = getFile(day).readLines()

    fun getString(day: Int): String = getFile(day).readText().replace("\r\n", "\n")

    fun getBlocks(day: Int): List<List<String>> = getString(day).split("\n\n").map { it.lines() }

    var day: Int = 0
    val isExample: Boolean get() = day > 1000
}