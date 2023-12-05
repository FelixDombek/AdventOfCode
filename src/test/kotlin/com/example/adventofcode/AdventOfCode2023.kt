package com.example.adventofcode

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.Scanner
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.system.measureTimeMillis


fun getFile(day: String): File {
    val infile = File("res/2023/${day.padStart(2, '0')}.txt")
    assertTrue("File does not exist: " + infile.absolutePath, infile.isFile)
    return infile
}
fun getInput(day: String): List<String> = getFile(day).readLines()

fun getString(day: String): String = getFile(day).readText()

class AdventOfCode2023 {
    @Test
    fun day1() {
        val input = getInput("1")
        val sum = input.sumOf { line ->
            "${line.first { it.isDigit() }}${line.last { it.isDigit() }}".toInt()
        }
        println("Day 1.1: $sum")
        assertEquals(54877, sum)

        val sum2 = input.sumOf { line ->
            val re = Regex("one|two|three|four|five|six|seven|eight|nine|1|2|3|4|5|6|7|8|9")
            fun toDigit(match: MatchResult): CharSequence =
                when (match.value) {
                    "one" -> "1"
                    "two" -> "2"
                    "three" -> "3"
                    "four" -> "4"
                    "five" -> "5"
                    "six" -> "6"
                    "seven" -> "7"
                    "eight" -> "8"
                    "nine" -> "9"
                    else -> match.value
                }

            val digit1 = re.find(line)!!.value.replace(re, ::toDigit)
            fun Regex.findLast(s: String) =
                (line.length-1 downTo 0).firstNotNullOf { this.matchAt(s, it) }
            val digit2 = re.findLast(line).value.replace(re, ::toDigit)

            "$digit1$digit2".toInt()
        }
        println("Day 1.2: $sum2")
        assertEquals(54100, sum2)
    }

    @Test
    fun day2() {
        val input = getInput("2")
        data class Round(val red: Int, val green: Int, val blue: Int) {
            fun canMatch(limits: Round) = red <= limits.red && green <= limits.green && blue <= limits.blue
            fun cube() = red * green * blue
        }
        data class Game(val id: Int, val rounds: List<Round>) {
            fun canMatch(numbers: Round) = rounds.all { it.canMatch(numbers) }
            fun minimumPossible() = rounds.fold(Round(0, 0, 0)) { acc, round ->
                Round(max(acc.red, round.red), max(acc.green, round.green), max(acc.blue, round.blue))
            }
        }
        // Game 2: 2 green, 2 blue, 16 red; 14 red; 13 red, 13 green, 2 blue; 7 red, 7 green, 2 blue
        val games = input.map { line ->
            val sc = Scanner(line).useDelimiter("[:;] ")
            val id = sc.skip("Game ").nextInt()
            val rounds = sc.asSequence().map { roundStr ->
                val colors = roundStr.split(", ")
                fun getCount(name: String) = colors.find { it.endsWith(name) }?.let { Scanner(it).nextInt() } ?: 0
                Round(getCount("red"), getCount("green"), getCount("blue"))
            }
            Game(id, rounds.toList())
        }
        val limits = Round(12, 13, 14)
        val idSum = games.filter { it.canMatch(limits) }.sumOf { it.id }
        println("Day 2.1: $idSum")
        assertEquals(2283, idSum)

        val minCubeSum = games.sumOf { it.minimumPossible().cube() }
        println("Day 2.2: $minCubeSum")
        assertEquals(78669, minCubeSum)
    }

    @Test
    fun day3() {
        val input = getInput("3")
        val sum = input.mapIndexed { lineNum, line ->
            Regex("\\d+").findAll(line).filter {
                for (i in max(lineNum - 1, 0)..min(lineNum + 1, input.size - 1)) {
                    for (j in max(it.range.first - 1, 0)..min(it.range.last + 1, input[lineNum].length - 1)) {
                        if (!(input[i][j].isDigit() || input[i][j] == '.')) return@filter true
                    }
                }
                false
            }.sumOf { it.value.toInt() }
        }.sum()
        println("Day 3.1: $sum")
        assertEquals(498559, sum)

        val gears = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
        input.forEachIndexed { lineNum, line ->
            Regex("\\d+").findAll(line).forEach {
                for (i in max(lineNum - 1, 0)..min(lineNum + 1, input.size - 1)) {
                    for (j in max(it.range.first - 1, 0)..min(it.range.last + 1, input[lineNum].length - 1)) {
                        if (input[i][j] == '*') {
                            gears.putIfAbsent(i to j, mutableListOf())
                            gears[i to j]!!.add(it.value.toInt())
                        }
                    }
                }
            }
        }
        val gearSum = gears.filter { it.value.size == 2 }.map { it.value[0] * it.value[1] }.sum()
        println("Day 3.2: $gearSum")
        assertEquals(72246648, gearSum)
    }

    @Test
    fun day4() {
        val input = getInput("4")
        val winCounts = input.map { line ->
            val sc = Scanner(line).useDelimiter("(Card\\s+\\d+: | \\| )")
            val winning = Scanner(sc.next()).asSequence().toList()
            val mine = Scanner(sc.next()).asSequence()
            mine.count { it in winning }
        }
        val sum = winCounts.sumOf { 2.0.pow((it - 1).toDouble()).toInt() }
        println("Day 4.1: $sum")
        assertEquals(25231, sum)

        val counts = MutableList(input.size) { 1 }
        winCounts.forEachIndexed { i, count ->
            for (id in i + 1..i + count) counts[id] += counts[i]
        }
        val sum2 = counts.sum()
        println("Day 4.2: $sum2")
        assertEquals(9721255, sum2)
    }

    @Test
    fun day5() {
        val input = getString("5").split("\n\n")
        val seeds = input[0].split(" ").drop(1).map { it.toLong() }
        val maps = input.drop(1).map { mapBlock ->
            mapBlock.split("\n").drop(1).map { mapLine ->
                val (to, from, len) = mapLine.split(" ").map { it.toLong() }
                from..<(from + len) to (to - from)
            }
        }
        fun mapSeeds(seeds: Sequence<Long>) = seeds.map { seed ->
            fun useMap(item: Long, m: List<Pair<LongRange, Long>>): Long {
                for (mapping in m) if (item in mapping.first) return item + mapping.second
                return item
            }
            maps.fold(seed) { item, m -> useMap(item, m) }
        }.min()

        val loc = mapSeeds(seeds.asSequence())
        println("Day 5.1: $loc")
        assertEquals(600279879, loc)

        val loc2 = seeds.chunked(2) { (seed, len) -> // takes 12 mins, needs optimization
            var m: Long
            val millis = measureTimeMillis {
                m = mapSeeds((seed..<seed + len).asSequence())
            }
            println("Day 5.2: $seed, len=$len took ${millis / 1000.0}s")
            m
        }.min()
        println("Day 5.2: $loc2")
        assertEquals(20191102, loc2)
    }
}