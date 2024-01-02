package com.fd.adventofcode

import org.junit.Test
import kotlin.test.assertEquals

class UtilTest : AdventBase(0) {
    @Test
    fun intToCircledTest() {
        assertEquals('②', 2.toCircled())
    }

    @Test
    fun iotaTest() {
        assertEquals(50, iota(50).first())
        assertEquals(5, iota().drop(5).first())
    }

    @Test
    fun rotatedLeftTest() {
        val input = listOf("abc", "def")
        val rotated = input.toMatrix().rotatedLeft().toStrings()
        assertEquals(listOf("cf", "be", "ad"), rotated)
    }

    @Test
    fun rotatedRightTest() {
        val input = listOf("abc", "def")
        val rotated = input.toMatrix().rotatedRight().toStrings()
        assertEquals(input.toMatrix().rotatedLeft().rotatedLeft().rotatedLeft().toStrings(), rotated)
        assertEquals(listOf("da", "eb", "fc"), rotated)
    }

    @Test
    fun moduloTest() {
        assertEquals(24, (-3).modulo(27))
    }

    @Test
    fun zipAllTest() {
        val s = sequenceOf(1,2,3)
        val s2 = sequenceOf(3,4,5,6)

        assertEquals(listOf(listOf(1,3), listOf(2,4), listOf(3,5)), zipAll(s, s2).toList())
    }
}