package com.example.adventofcode

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.Scanner

fun getInput(day: String): List<String> {
    val infile = File("advent2023-" + day.padStart(2, '0') + ".input.txt")
    assertTrue("File does not exist: " + infile.absolutePath, infile.isFile)
    return infile.readLines()
}

class AdventOfCode2023 {
    @Test
    fun door01() {
        val input = getInput("1")
        val sum = input.sumOf { line ->
            "${line.first { it.isDigit() }}${line.last { it.isDigit() }}".toInt()
        }
        println("Door 1.1: $sum")

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
        println("Door 1.2: $sum2")
    }

    @Test
    fun door02() {
        val input = getInput("2")
        data class Round(val red: Int, val green: Int, val blue: Int) {
            fun canMatch(numbers: Round) = red <= numbers.red && green <= numbers.green && blue <= numbers.blue
            fun cube() = red * green * blue
        }
        data class Game(val id: Int, val rounds: List<Round>) {
            fun canMatch(numbers: Round) = rounds.all { it.canMatch(numbers) }
            fun minimumPossible() = rounds.fold(Round(0, 0, 0)) { acc, round ->
                Round(maxOf(acc.red, round.red), maxOf(acc.green, round.green), maxOf(acc.blue, round.blue))
            }
        }
        // Game 2: 2 green, 2 blue, 16 red; 14 red; 13 red, 13 green, 2 blue; 7 red, 7 green, 2 blue
        val games = input.map { line ->
            val sc = Scanner(line).useDelimiter("[:;] ")
            val id = sc.skip("Game ").nextInt()
            val rounds = sc.asSequence().map { roundStr ->
                val colors = roundStr.split(", ")
                fun getCount(name: String) = colors.find { it.contains(name) }?.let { Scanner(it).nextInt() } ?: 0
                Round(getCount("red"), getCount("green"), getCount("blue"))
            }
            Game(id, rounds.toList())
        }
        val numbers = Round(12, 13, 14)
        val idSum = games.filter { it.canMatch(numbers) }.sumOf { it.id }
        println("Door 2.1: $idSum")
        assertEquals(2283, idSum)

        val minCubeSum = games.sumOf { it.minimumPossible().cube() }
        println("Door 2.2: $minCubeSum")
        assertEquals(78669, minCubeSum)
    }
}