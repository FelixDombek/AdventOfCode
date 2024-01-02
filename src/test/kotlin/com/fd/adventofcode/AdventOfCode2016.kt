package com.fd.adventofcode

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class AdventOfCode2016 : AdventBase(2016) {
    @Test
    fun day1() {
        val input = getString(1).split(", ").map { it[0] to it.substring(1).toInt() }

        fun next(x: Int, y: Int, dir: Char, lr: Char, steps: Int) = when (dir) {
            '^' -> if (lr == 'L') x-steps to y to3 '<' else x+steps to y to3 '>'
            'v' -> if (lr == 'L') x+steps to y to3 '>' else x-steps to y to3 '<'
            '<' -> if (lr == 'L') x to y+steps to3 'v' else x to y-steps to3 '^'
            else -> if (lr == 'L') x to y-steps to3 '^' else x to y+steps to3 'v'
        }
        val (x, y) = input.fold(0 to 0 to3 '^') { (x, y, dir), (lr, steps) -> next(x, y, dir, lr, steps) }

        assertEquals("Day 1.1", 279, abs(x) + abs(y))

        val visited = mutableSetOf(0 to 0)
        fun visit(fromX: Int, fromY: Int, toX: Int, toY: Int): Pair<Int, Int>? {
            if (fromX == toX) { for (curY in if (fromY < toY) fromY+1..toY else fromY-1 downTo toY ) if (!visited.add(fromX to curY)) return fromX to curY }
            else              { for (curX in if (fromX < toX) fromX+1..toX else fromX-1 downTo toX ) if (!visited.add(curX to fromY)) return curX to fromY }
            return null
        }
        fun path() = input.fold(0 to 0 to3 '^') { (x, y, dir), (lr, steps) ->
            next(x, y, dir, lr, steps).also { (newX, newY, newDir) -> visit(x, y, newX, newY)?.let { return it to3 newDir } }
        }
        val (x2, y2) = path()

        assertEquals("Day 1.2", 163, abs(x2) + abs(y2))
    }

    @Test
    fun day2() {
        val input = getInput(2)
        // the starting position turns out to be irrelevant, we end up in the same places anyway
        fun code(numpad: Array<Array<Char>>) = input.joinToString("") { it.fold(2 to 2) { (x, y), dir -> when (dir) {
            'U' -> x to max(y - 1, numpad.indices.first)
            'D' -> x to min(y + 1, numpad.indices.last)
            'L' -> max(x - 1, numpad.indices.first) to y
            else -> min(x + 1, numpad.indices.last) to y
        }.let { (newX, newY) -> if (numpad[newY][newX] == '0') x to y else newX to newY } }.let { (x, y) -> numpad[y][x].toString() } }

        val numpad1 = arrayOf(arrayOf('1','2','3'),
                              arrayOf('4','5','6'),
                              arrayOf('7','8','9'))
        assertEquals("Day 2.1", "35749", code(numpad1))

        val numpad2 = arrayOf(arrayOf('0','0','1','0','0'),
                              arrayOf('0','2','3','4','0'),
                              arrayOf('5','6','7','8','9'),
                              arrayOf('0','A','B','C','0'),
                              arrayOf('0','0','D','0','0'))
        assertEquals("Day 2.2", "9365C", code(numpad2))
    }

    @Test
    fun day3() {
        val triangles = getInput(3).map { Scanner(it).findAllInt() }
        fun possible(tri: List<Int>) = tri.indices.all { tri[it] < tri[(it+1)%3] + tri[(it+2)%3] }
        val count = triangles.filter { possible(it) }.count()
        assertEquals("Day 3.1", 862, count)

        val triangles2 = triangles.transposed().flatten().chunked(3)
        val count2 = triangles2.filter { possible(it) }.count()
        assertEquals("Day 3.2", 1577, count2)
    }

    @Test
    fun day4() {
        val re = Regex("(?<name>[a-z-]+)-(?<sec>\\d+)\\[(?<checksum>\\w{5})]")
        val rooms = getInput(4).map { with (re.matchEntire(it)!!.groups) { get("name")!!.value to get("sec")!!.value.toInt() to3 get("checksum")!!.value } }
        fun check(name: String, checksum: String) = name.replace("-", "")
            .associate { c -> c to name.count { c == it } }.toList()
            .sortedWith(compareBy({-it.second}, {it.first})).take(5)
            .joinToString("") { it.first.toString() } == checksum
        val real = rooms.filter { (name, _, checksum) -> check(name, checksum) }
        val sum = real.sumOf { it.second }
        assertEquals("Day 4.1", 245102, sum)

        fun shift(c: Char, n: Int) = (((c.code - 'a'.code) + (n % 26)) % 26 + 'a'.code).toChar()
        fun shift(w: String, n: Int) = w.map { shift(it, n) }.joinToString("")
        val northpoleSec = real.first { (name, sec, _) -> "northpole" in name.split("-").map { shift(it, sec) }.joinToString(" ") }.second
        assertEquals("Day 4.2", 324, northpoleSec)
    }

    @Test
    fun day5() {
        // changed from 5 to 4 zeroes for faster runtime.
        val input = "reyedfim"
        val pass = iota().map { md5("$input$it").toHex() }
            .filter { it.startsWith("0000") }
            .map { it[5].also { println(it) } }.take(8).joinToString("")
        // assertEquals("Day 5.1 - 5x0", "f97c354d", pass)
        assertEquals("Day 5.1 - 4x0", "21a0f6f7", pass)

        val pass2 = MutableList(8) { '_' }
        iota().takeWhile { '_' in pass2 }.map { md5("$input$it").toHex() }
            .filter { it.startsWith("0000") && it[5].isDigit() && it[5].digitToInt().let { it < 8 && pass2[it] == '_' } }
            .forEach { pass2[it[5].digitToInt()] = it[6] ; println(pass2) }
        // assertEquals("Day 5.2 - 5x0", "863dde27", pass2.joinToString(""))
        assertEquals("Day 5.1 - 4x0", "8944e670", pass2.joinToString(""))
    }

    @Test
    fun day6() {
        val input = getInput(6)
        val msg = input.transposed().map { it.groupBy { it }.maxByOrNull { it.value.size }!!.key }.joinToString("")
        assertEquals("Day 6.1", "wkbvmikb", msg)

        val msg2 = input.transposed().map { it.groupBy { it }.minByOrNull { it.value.size }!!.key }.joinToString("")
        assertEquals("Day 6.1", "evakwaga", msg2)
    }

    @Test
    fun day7() {
        val input = getInput(7)
        val abba = Regex("(?!(.)\\1)(.)(.)\\3\\2")
        val bracketedAbba = Regex("\\[[^\\[\\]]*?${abba.pattern}[^\\[\\]]*?]")
        val count = input.filter { !bracketedAbba.containsMatchIn(it) && abba.containsMatchIn(it) }.count()

        assertEquals("Day 7.1", 115, count)

        val aba = Regex("(?!([a-z])\\1)([a-z])([a-z])\\2")
        val count2 = input.map { ip ->
            ip.indices.filter { ip.substring(it).findAnyOf(listOf("[", "]")).let { it == null || it.second.single() == '[' } }
                .mapNotNull { i -> aba.matchAt(ip, i)?.value }
                .filter { v ->
                    ip.indices.filter { ip.substring(it).findAnyOf(listOf("[", "]")).let { it != null && it.second.single() == ']' } }
                    .mapNotNull { i2 -> Regex("${v[1]}${v[0]}${v[1]}").matchAt(ip, i2)?.value }.isNotEmpty()
                }.isNotEmpty()
        }.count { it }
        assertEquals("Day 7.2", 231, count2)
    }

    @Test
    fun day8() {
        val screen = List(6) { MutableList(50) { false } }
        fun rect(x: Int, y: Int) { for (r in 0..<y) for (c in 0..<x) screen[r][c] = true }
        fun rotateRow(r: Int, by: Int) { Collections.rotate(screen[r], by) }
        fun rotateCol(c: Int, by: Int) { val t = screen.column(c) ; Collections.rotate(t, by) ; screen.setColumn(c, t) }
        getInput(8).forEach {
            val (a, b) = Scanner(it).useDelimiter("[a-z =]+").findAllInt()
            if (it.startsWith("rect")) rect(a, b) else if (it.startsWith("rotate row")) rotateRow(a, b) else rotateCol(a, b)
        }
        val lit = screen.sumOf { it.count { it } }
        assertEquals("Day 8.1", 106, lit)

        val word = screen.first().indices.joinToString("") { "${screen.column(it).count { it }}" }.chunked(5).joinToString("") { when (it.toInt()) {
            42220 -> "C" ; 62210 -> "F" ; 61110 -> "L" ; 63320 -> "E"
            42240 -> "O" ; 21312 -> "Y" ; 33320 -> "S" ; else -> "?"
        } }
        assertEquals("Day 8.2", "CFLELOYFCS", word)
    }

    @Test
    fun day9() {
        val input = getString(9)
        fun decompress(s: String, v2: Boolean, cache: MutableMap<String, Long>?): Long {
            cache?.get(s)?.let { return it }
            var i = 0
            var len = 0L
            while (i < s.length) {
                if (s[i] != '(') { ++len ; ++i ; continue }
                val closeAt = s.indexOf(')', i)
                val (c, t) = s.substring(i + 1, closeAt).split("x").map { it.toInt() }
                i = closeAt + c + 1
                repeat(t) { len += if (v2) decompress(s.substring(closeAt + 1, i), true, cache) else i - (closeAt + 1L) }
            }
            return len.also { cache?.set(s, len) ; println("$i: $len - $s") }
        }
        val len = decompress(input, false, null)
        assertEquals("Day 9.1", 97714, len)

        val len2 = decompress(input, true, mutableMapOf())
        assertEquals("Day 9.2", 10762972461, len2)
    }

    @Test
    fun day10() {
        val input = getInput(10)
        val re = Regex("(value (?<val>\\d+) goes to (?<valId>bot \\d+))|((?<fromId>bot \\d+) gives low to (?<lowTo>\\w+ \\d+) and high to (?<highTo>\\w+ \\d+))")
        data class Bot(val hold: MutableList<Int> = mutableListOf(), var lowTo: String = "", var highTo: String = "")
        val bots = mutableMapOf<String, Bot>()
        input.forEach { with (re.matchEntire(it)!!.groups) {
            if (get("val") != null) {
                val v = get("val")!!.value.toInt()
                val id = get("valId")!!.value
                bots.putIfAbsent(id, Bot())
                bots[id]!!.hold.add(v)
            } else {
                val fromId = get("fromId")!!.value
                bots.putIfAbsent(fromId, Bot())
                with (bots[fromId]!!) {
                    lowTo = get("lowTo")!!.value
                    bots.putIfAbsent(lowTo, Bot())
                    highTo = get("highTo")!!.value
                    bots.putIfAbsent(highTo, Bot())
                }
            }
        } }
        var botFor17and61 = ""
        while (true) {
            val idbot = bots.firstNotNullOfOrNull { (id, bot) -> if (id.startsWith("bot") && bot.hold.size >= 2) (id to bot) else null }
            if (idbot == null) break
            val (id, bot) = idbot
            val (low, high) = bot.hold.sorted()
            bot.hold.clear()
            bots[bot.lowTo]!!.hold.add(low)
            bots[bot.highTo]!!.hold.add(high)
            if (low == 17 && high == 61) { botFor17and61 = id.split(" ").last() }
        }
        assertEquals("Day 10.1", "56", botFor17and61)

        val out = bots["output 0"]!!.hold.single() * bots["output 1"]!!.hold.single() * bots["output 2"]!!.hold.single()
        assertEquals("Day 10.2", 7847, out)
    }

    @Test
    fun day11() {
        //The first floor contains a strontium generator, a strontium-compatible microchip, a plutonium generator, and a plutonium-compatible microchip.
        //The second floor contains a thulium generator, a ruthenium generator, a ruthenium-compatible microchip, a curium generator, and a curium-compatible microchip.
        //The third floor contains a thulium-compatible microchip.
        //The fourth floor contains nothing relevant.
        data class State(val floors: List<List<String>>, val elevator: Int, val steps: Int, val prev: State?) {
            override fun equals(other: Any?) = (this === other) || (other is State && floors == other.floors && elevator == other.elevator)
            override fun hashCode() = floors.hashCode() + elevator
            override fun toString() = "$steps (${weight()}): $elevator $floors"
            // these weights are suboptimal for pt1, but make pt2 quite fast, which pays off in total
            fun weight() = 2*steps + floors.mapIndexed{ i, f -> (3-i) * 3*f.size + (3-i)*f.fold(0) { w, o -> w + (o.last() - '1') } / 3 }.sum()
            fun end() = floors.subList(0, 3).all { it.isEmpty() }
        }

        fun valid(floor: List<String>) =
            floor.firstOrNull { it.startsWith("M") && "G${it[1]}" !in floor && floor.firstOrNull() { it[0] == 'G' } != null } == null

        fun newFloors(floors: List<List<String>>, newFloor: List<String>, elevator: Int)
            = floors.take(elevator) + listOf(newFloor) + floors.drop(elevator + 1)
        fun newState(s: State, elems: List<String>, diffElevator: Int) = State(
            newFloors(
                newFloors(s.floors, s.floors[s.elevator].filter { it !in elems }, s.elevator),
                s.floors[s.elevator + diffElevator] + elems,
                s.elevator + diffElevator),
            s.elevator + diffElevator,
            s.steps + 1,
            s)

        val seen = mutableSetOf<State>()
        fun genNext(s: State): List<State>? { with (s.floors[s.elevator]) {
            if (s.end()) return null
            val toElevator = /* one item */ filter { it1 -> valid(filter { it != it1 }) }.map { listOf(it) } +
                             /* two items */ map { it1 -> filter { it2 ->
                                                            it1 > it2 &&
                                                            valid(listOf(it1, it2)) &&
                                                            valid(filter { it != it1 && it != it2 })
                                                          }.map { listOf(it1, it) } }.flatten()

            val new = listOf(1, -1).map { s.elevator + it }.filter { it in s.floors.indices }.map { newElevator ->
                toElevator.filter { valid(s.floors[newElevator] + it) }.map { newState(s, it, newElevator-s.elevator) }.filter { it !in seen }
            }.flatten()
            seen.addAll(new)
            return new
        } }

        val queue = PriorityQueue<State>(compareBy { it.weight() })
        fun search(initialTask: List<List<String>>): State {
            val initial = State(initialTask, 0, 0, null)
            queue.clear()
            queue.add(initial)
            seen.clear()
            seen.add(initial)
            var counter = 0
            while (true) {
                val s = queue.remove()
                val next = genNext(s) ?: return s
                //if (++counter % 1000 == 0) println("$counter: $s")
                queue.addAll(next)
            }
        }

        val task = listOf(
            listOf("G1", "M1", "G2", "M2"),
            listOf("G3", "G4", "M4", "G5", "M5"),
            listOf("M3"),
            listOf()
        )
        val end = search(task)
        println("q: ${queue.size}")
        generateSequence(end) { it.prev }.toList().reversed().forEach { it.println() }
        assertEquals("Day 11.1", 37, end.steps)

        val task2 = listOf(
            listOf("G1", "M1", "G2", "M2", "G6", "M6", "G7", "M7"),
            listOf("G3", "G4", "M4", "G5", "M5"),
            listOf("M3"),
            listOf()
        )
        val end2 = search(task2)
        println("q: ${queue.size}")
        generateSequence(end2) { it.prev }.toList().reversed().forEach { it.println() }
        assertEquals("Day 11.1", 61, end2.steps)
    }

    @Test
    fun day12() {
        val ops = getInput(12).map { it.split(" ") }
        fun toReg(s: String) = s.first() - 'a'
        fun isReg(s: String) = s.first() >= 'a' && s.first() <= 'd'
        fun calc(reg: MutableList<Int>): List<Int> {
            var pc = 0
            while (pc in ops.indices) { with (ops[pc]) { when (get(0)) {
                "cpy" -> reg[toReg(get(2))] = if (isReg(get(1))) reg[toReg(get(1))] else get(1).toInt()
                "inc" -> ++reg[toReg(get(1))]
                "dec" -> --reg[toReg(get(1))]
                "jnz" -> if (isReg(get(1)) && reg[toReg(get(1))] != 0 || !isReg(get(1)) && get(1).toInt() != 0) pc += (get(2).toInt() - 1) else {}
                else -> throw IllegalStateException("???")
            } }.also { ++pc } }
            return reg
        }

        assertEquals("Day 12.1", 318077, calc(mutableListOf(0,0,0,0))[0])
        assertEquals("Day 12.2", 9227731, calc(mutableListOf(0,0,1,0))[0])
    }

    @Test
    fun day13() {
        fun isOpenSpace(x: Int, y: Int) = (x*x + 3*x + 2*x*y + y + y*y + 1358).countOneBits() % 2 == 0
        fun step(cur: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
            val next = mutableSetOf<Pair<Int, Int>>()
            for (plot in cur) {
                listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1).forEach {
                    val (newX, newY) = (plot.first to plot.second) + it
                    if (newX >= 0 && newY >= 0 && isOpenSpace(newX, newY)) next.add(newX to newY)
                }
            }
            return next
        }

        var cur = setOf(1 to 1)
        var steps = 0
        while (31 to 39 !in cur) cur = step(cur).also { ++steps }
        assertEquals("Day 13.1", 96, steps)

        val visited = mutableSetOf(1 to 1)
        var cur2 = setOf(1 to 1)
        repeat(50) { cur2 = step(cur2) ; visited.addAll(cur2) }
        assertEquals("Day 13.2", 141, visited.size)
    }

    @Test
    fun day14() {
        val salt = /*"abc"*/"zpqevtbw"
        var i = 0
        val tripletRe = Regex("([0-9a-f])\\1\\1")
        val window = 1001
        generateSequence { md5("$salt${i++}").toHex() }.windowed(window).filter { list ->
            tripletRe.find(list.first()).let { m -> m != null &&
                    list.subList(1, list.size).find { m.value.first().toString().repeat(5) in it } != null }
        }.take(64).forEach { }
        assertEquals("Day 14.1", 16106, i - window)

        fun stretchedMD5(s: String) = (0..2016).fold(s) { h, _ -> md5(h).toHex() }
        i = 0
        generateSequence { stretchedMD5("$salt${i++}") }.windowed(window).filter { list ->
            tripletRe.find(list.first()).let { m -> m != null &&
                    list.subList(1, list.size).find { m.value.first().toString().repeat(5) in it } != null }
        }.take(64).forEach { it.first().println() }
        assertEquals("Day 14.2", 22423, i - window)
    }

    @Test
    fun day15() {
        fun disc(num: Int, slots: Int, offset: Int) = (slots - offset - num).toLong() to slots.toLong()
        val input = getInput(15).map { Scanner(it).useDelimiter("\\D+").findAllInt().let { disc(it[0], it[1], it[3]) } }

        // (a) the simple way
        fun seq(off: Int, p: Int) = "|${"x".repeat(p-1)}".asSequence().repeat("o".repeat(off.modulo(p)).asSequence())
        val zipSeq = zipAll(input.map { seq(it.first.toInt(), it.second.toInt()) })
        val allPipes = zipSeq.indexOfFirst { it.all { it == '|' } }
        assertEquals("Day 15.1a", 148737, allPipes)

        fun findOffsetAndPeriod(aoff: Long, ap: Long, boff: Long, bp: Long): Pair<Long, Long> {
            val (start, inc, diff, divide) = if (ap > bp) listOf(aoff, ap, boff, bp) else listOf(boff, bp, aoff, ap)
            var offset = start
            while ((offset - diff).modulo(divide) != 0L) {
                offset += inc
            }
            // period calculation, not needed but may come in handy later
            val period = lcm(ap, bp)
            if (offset == period) offset = 0
            return offset to period
        }
        val (offset, _) = input.reduce { (accOff, accP), (inOff, inP) -> findOffsetAndPeriod(accOff, accP, inOff, inP) }
        assertEquals("Day 15.1b", 148737, offset)

        val input2 = input + disc(7, 11, 0)
        val (offset2, _) = input2.reduce { (accOff, accP), (inOff, inP) -> findOffsetAndPeriod(accOff, accP, inOff, inP) }
        assertEquals("Day 15.2", 2353212, offset2)
    }

    @Test
    fun day16() {
        fun dragoncurve(a: String) = "${a}0${a.reversed().replace(Regex("[01]")) { m -> if (m.value.single() == '0') "1" else "0" }}"
        fun checksum(s: String) = iota().scan(s) { cs, _ ->
            cs.windowedSequence(2, 2) { if (it.first() == it.last()) '1' else '0' }.joinToString("")
        }.first { it.length % 2 != 0 }
        fun fillcheck(s: String, l: Int) = checksum(iota().scan(s) { fill, _ -> dragoncurve(fill) }.first { it.length >= l }.take(l))

        val input = "01111010110010011"
        val targetLen = 272
        assertEquals("Day 16.1", "00100111000101111", fillcheck(input, targetLen))

        val targetLen2 = 35651584
        assertEquals("Day 16.2", "11101110011100110", fillcheck(input, targetLen2))
    }

    @Test
    fun day17() {
        val input = "pslxynzg"
        fun open(dir: Char, hash: String) = hash[when (dir) { 'U' -> 0 ; 'D' -> 1 ; 'L' -> 2 ; else -> 3 }] in "bcdef"
        fun next(prev: Pair<Int, Int>, dir: Char) =
            prev + when (dir) { 'U' -> 0 to -1 ; 'D' -> 0 to 1 ; 'L' -> -1 to 0 ; else -> 1 to 0 }
        data class State(val path: String, val cur: Pair<Int, Int>)
        fun step(s: State) = s.cur.first in 0..3 && s.cur.second in 0..3 &&
                open(s.path.last(), md5(input + s.path.dropLast(1)).toHex())
        fun step(q: MutableList<State>, l: MutableList<State>) = when (val s = q.removeFirstOrNull()) {
            null -> false
            else -> { "UDLR".map { State(s.path + it, next(s.cur, it)) }.filter { step(it) }
                            .forEach { (if (it.cur == 3 to 3) l else q).add(it) } ; true }
        }

        val q = mutableListOf(State("", 0 to 0))
        val l = mutableListOf<State>()
        while (step(q, l)) {}

        val shortest = l.minBy { it.path.length }.path
        assertEquals("Day 17.1", "DDRRUDLRRD", shortest)

        val longest = l.maxBy { it.path.length }.path.length
        assertEquals("Day 17.2", 488, longest)
    }

    @Test
    fun day18() {
        val first = getString(18)
        fun next(row: String) = ".$row.".windowed(3) { if (it[0] != it[2]) '^' else '.' }.joinToString("")
        var count = 0
        var cur = first
        repeat(40) { count += cur.count { it == '.' } ; cur = next(cur) }
        assertEquals("Day 18.1", 2013, count)

        var count2 = 0
        var cur2 = first
        repeat(400_000) { count2 += cur2.count { it == '.' } ; cur2 = next(cur2) }
        assertEquals("Day 18.2", 20006289, count2)
    }

    @Test
    fun day19() {
        val elves = 3014603
        var l = iota(1).take(elves).toList()
        while (l.size > 1) {
            val takes1 = l.size % 2 != 0
            l = l.filterIndexed { i, e -> if (i == 0) !takes1 else i % 2==0 }
        }
        assertEquals("Day 19.1", 1834903, l.single())

        val l2 = iota(1).take(elves).toMutableList()
        fun opposite(i: Int, size: Int) = (i + size/2) % size
        fun step(i: Int): Int {
            var ii = i
            var opp = opposite(ii, l2.size)
            if (opp < i) {
                Collections.rotate(l2, -i)
                ii = 0
                opp += opposite(ii, l2.size)
            }
            l2.removeAt(opp)
            return (ii + 1) % l2.size
        }
        var cur = 0
        while (l2.size > 1) cur = step(cur)
        assertEquals("Day 19.2", 1420280, l2.single())
    }

    @Test
    fun day20() {
        val input = getInput(20).map { it.split("-").map { it.toLong() }.let { it[0]..it[1] } }
        fun nextAllowed(u: Long): Long {
            val nextRange = input.filter { it.first <= u && it.last > u }.maxByOrNull { it.first }
            return if (nextRange == null) u else nextAllowed(nextRange.last + 1)
        }
        val allowed = nextAllowed(0)
        assertEquals("Day 20.1", 17348574, allowed)

        fun nextBlocked(u: Long): Long {
            return input.map { it.first }.filter { it >= u }.minOrNull() ?: (UInt.MAX_VALUE.toLong() + 1)
        }
        var count = 0L
        var cur = 0L
        while (cur <= UInt.MAX_VALUE.toLong()) {
            val a = nextAllowed(cur)
            cur = nextBlocked(a)
            count += (cur - a)
        }
        assertEquals("Day 20.2", 104, count)
    }

    @Test
    fun day21() {

    }
}