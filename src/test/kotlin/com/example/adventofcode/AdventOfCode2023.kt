package com.example.adventofcode

import org.junit.Assert.*
import org.junit.Test
import java.util.Scanner
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class AdventOfCode2023 : AdventBase(2023) {
    @Test
    fun day1() {
        val input = getInput(1)
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
        val input = getInput(2)
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
        val input = getInput(3)
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
        val input = getInput(4)
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
        val input = getString(5).split("\n\n")
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

        // naive: takes 12 minutes
        //val loc2 = seeds.chunked(2) { (seed, len) ->
        //    mapSeeds((seed..<seed + len).asSequence())
        //}.min()

        // optimized: takes 2 ms. 240000x speedup!
        fun mapSeedsStepped(seeds: LongRange): Long {
            fun useMap(item: Long, m: List<Pair<LongRange, Long>>): Pair<Long, Long> {
                for (mapping in m) if (item in mapping.first) return item + mapping.second to mapping.first.last - item + 1
                return item to m.fold(Long.MAX_VALUE) { len, mapping ->
                    if (mapping.first.first > item) min(mapping.first.first - item, len) else len
                }
            }
            fun useMaps(seed: Long): Pair<Long, Long> =
                maps.fold(seed to Long.MAX_VALUE) { (item, len), m ->
                    val (newItem, newLen) = useMap(item, m)
                    newItem to min(len, newLen)
                }

            var seed = seeds.first()
            var minLoc = Long.MAX_VALUE
            while (seed <= seeds.last()) {
                val (newLoc, step) = useMaps(seed)
                minLoc = min(newLoc, minLoc)
                seed += step
            }
            return minLoc
        }

        val loc2 = seeds.chunked(2) { (seed, len) ->
            mapSeedsStepped(seed..<seed + len)
        }.min()
        println("Day 5.2: $loc2")
        assertEquals(20191102, loc2)
    }

    @Test
    fun day6() {
        val (times, dists) = getInput(6).map { Scanner(it).asSequence().drop(1).map { it.toInt() }.toList() }
        val numWays = times.zip(dists).map { (time, dist) ->
            (1..<time).map { hold -> hold * (time - hold) }.count { it > dist }
        }.reduce { acc, elem -> acc * elem }
        println("Day 6.1: $numWays")
        assertEquals(114400, numWays)

        val time = times.joinToString("").toLong()
        val dist = dists.joinToString("").toLong()
        val numWays2 = (1..<time).asSequence().map { hold -> hold * (time - hold) }.count { it > dist }
        println("Day 6.2: $numWays2")
    }
}