package com.fd.adventofcode

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class AdventOfCode2017 extends AdventBase(2017) with AnyFunSuite with Matchers {

  test("day1") {
    val input = getString(1)
    val floor = input.count(_ == '(') - input.count(_ == ')')
    assert(floor == 74, "2017.1.1")

    var floor2 = 0
    val i = input.indexWhere {
      case '(' => floor2 += 1
      case ')' => floor2 -= 1
        floor2 == -1
    }
    (i + 1) shouldEqual 1795
  }
}


