package com.fd.adventofcode

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Scanner

class AdventOfCode2015 : AdventBase(2015) {
    @Test
    fun `day 1, not quite lisp`() {
        val input = getString(1)
        val floor = input.count { it == '(' } - input.count { it == ')' }
        assertEquals("Day 1.1", 74, floor)

        var floor2 = 0
        val i = input.indexOfFirst { if (it == '(') ++floor2 else --floor2 ; floor2 == -1 }
        assertEquals("Day 1.2", 1795, i + 1)
    }

    @Test
    fun `day 2, i was told there would be no math`() {
        val input = getInput(2).map { Scanner(it).useDelimiter("x").findAllInt().sorted() }
        val paper = input.sumOf { (a, b, c) -> 3*a*b + 2*a*c + 2*b*c }
        assertEquals("Day 2.1", 1606483, paper)

        val ribbon = input.sumOf { (a, b, c) -> 2*a + 2*b + a*b*c }
        assertEquals("Day 2.2", 3842356, ribbon)
    }

    @Test
    fun `day 3, perfectly spherical houses in a vacuum`() {
        val input = getString(3)
        var visited = mutableSetOf(0 to 0)
        fun update(pos: Pair<Int, Int>, c: Char) =
            (pos + when (c) { '^' -> -1 to 0 ; 'v' -> 1 to 0 ; '<' -> 0 to -1 ; else -> 0 to 1 }).also { visited.add(it) }
        input.fold(0 to 0) { pos, c -> update(pos, c) }
        assertEquals("Day 3.1", 2081, visited.size)

        visited = mutableSetOf(0 to 0)
        input.foldIndexed((0 to 0) to (0 to 0)) { i, (s, r), c -> if (i % 2 == 0) update(s, c) to r else s to update(r, c) }
        assertEquals("Day 3.2", 2341, visited.size)
    }

    @Test
    fun `day 4, the ideal stocking stuffer`() {
        val input = "ckczppom"
        val five0 = iota().first { md5("$input$it").toHex().startsWith("00000") }
        assertEquals("Day 4.1", 117946, five0)

        val six0 = iota().first { md5("$input$it").toHex().startsWith("000000") }
        assertEquals("Day 4.2", 3938038, six0)
    }

    @Test
    fun `day 5, doesn't he have intern-elves for this`() {
        val input = getInput(5)
        val vowels = Regex("[aeiou].*[aeiou].*[aeiou]")
        val pair = Regex("(.)\\1")
        val blocked = Regex("ab|cd|pq|xy")
        val count = input.count { !blocked.containsMatchIn(it) && vowels.containsMatchIn(it) && pair.containsMatchIn(it) }
        assertEquals("Day 5.1", 255, count)

        val double = Regex("(..).*\\1")
        val between = Regex("(.).\\1")
        val count2 = input.count { double.containsMatchIn(it) && between.containsMatchIn(it) }
        assertEquals("Day 5.2", 55, count2)
    }

    @Test
    fun `day 6, probably a fire-hazard`() {
        val ops = getInput(6).map { it.replace("turn ", "turn") }.map { with (Scanner(it).useDelimiter("[ ,]")) {
            Triple(next(), nextInt() to nextInt(), skip(" through").nextInt() to nextInt())
        } }

        val lights = List(1000) { MutableList(1000) { false } }
        ops.forEach { (op, from, to) ->
            for (x in from.first..to.first) for (y in from.second..to.second)
                lights[y][x] = if (op == "toggle") !lights[y][x] else op == "turnon"
        }
        val count = lights.sumOf { it.count { it } }
        assertEquals("Day 6.1", 569999, count)

        val lights2 = List(1000) { MutableList(1000) { 0 } }
        ops.forEach { (op, from, to) ->
            for (x in from.first..to.first) for (y in from.second..to.second) when (op) {
                "turnon" -> ++lights2[y][x]
                "turnoff" -> if (lights2[y][x] > 0) --lights2[y][x]
                "toggle" -> lights2[y][x] += 2
            }
        }
        val brightness = lights2.sumOf { it.sum() }
        assertEquals("Day 6.2", 17836115, brightness)
    }

    @Test
    fun `day 7, some assembly required`() {
        data class Op(val num: UShort? = null, val id: String? = null,
                      val lhsNum: UShort? = null, val lhsId: String? = null,
                      val op: String? = null,
                      val rhsNum: UShort? = null, val rhsId: String? = null)
        val re = Regex("(?<num>\\d+)|(?<id>\\w+)|(NOT ((?<notNum>\\d+)|(?<notId>\\w+)))|" +
                "(((?<lhsNum>\\d+)|(?<lhsId>\\w+)) (?<op>\\w+) ((?<rhsNum>\\d+)|(?<rhsId>\\w+)))")
        fun toOp(s: String) = with (re.matchEntire(s)!!.groups) { when {
            get("num") != null -> Op(num = get("num")!!.value.toUShort())
            get("id") != null -> Op(id = get("id")!!.value)
            get("notId") != null -> Op(op = "NOT", rhsId = get("notId")!!.value)
            get("notNum") != null -> Op(op = "NOT", rhsNum = get("notNum")!!.value.toUShort())
            else -> Op(lhsNum = get("lhsNum")?.value?.toUShort(), lhsId = get("lhsId")?.value,
                       op = get("op")!!.value,
                       rhsNum = get("rhsNum")?.value?.toUShort(), rhsId = get("rhsId")?.value)
        } }
        val circuit = getInput(7).associate { with (it.split(" -> ")) {
            get(1) to (toOp(get(0)) to mutableListOf<UShort?>(null))
        } }

        fun readWire(id: String): UShort {
            val (source, value) = circuit[id]!!
            value.single()?.let { return it }
            fun calc(): UShort {
                source.num?.let { return it }
                source.id?.let { return readWire(it) }
                val lhs = source.lhsNum ?: source.lhsId?.let { readWire(it) }
                val rhs = source.rhsNum ?: readWire(source.rhsId!!)
                return when (source.op!!) {
                    "NOT" -> rhs.inv()
                    "AND" -> lhs!! and rhs
                    "OR" -> lhs!! or rhs
                    "LSHIFT" -> (lhs!!.toInt() shl rhs.toInt()).toUShort()
                    "RSHIFT" -> (lhs!!.toInt() shr rhs.toInt()).toUShort()
                    else -> throw IllegalArgumentException("$id - $source")
                }
            }
            return calc().also { value[0] = it }
        }

        val aVal = readWire("a")
        assertEquals("Day 7.1", 956.toUShort(), aVal)

        circuit.values.forEach { (_, v) -> v[0] = null }
        circuit["b"]!!.second[0] = aVal

        val aVal2 = readWire("a")
        assertEquals("Day 7.2", 40149.toUShort(), aVal2)
    }

    @Test
    fun `day 8, matchsticks`() {
        val input = getInput(8)
        val codeLen = input.sumOf { it.length }
        val inMem = input.sumOf {
            var esc = false
            var escx1 = false
            var escx2 = false
            it.substring(1, it.lastIndex).count { when {
                !esc && it == '\\' -> { esc = true ; false }
                esc && (it == '\\' || it == '"') -> { esc = false ; true }
                esc && it == 'x' -> { escx1 = true ; false }
                esc && escx1 && !escx2 && it.isHexDigit() -> { escx2 = true ; false }
                esc && escx1 && escx2 && it.isHexDigit() -> { esc = false ; escx1 = false ; escx2 = false ; true }
                else -> true
            } }
        }
        val diff = codeLen - inMem
        assertEquals("Day 8.1", 1342, diff)

        val asString = input.sumOf { it.length + it.count { it == '\\' || it == '"' } + 2 }
        val diff2 = asString - codeLen
        assertEquals("Day 8.2", 2074, diff2)
    }
}