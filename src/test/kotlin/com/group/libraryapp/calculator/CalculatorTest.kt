package com.group.libraryapp.calculator

import org.junit.jupiter.api.Assertions.*

fun main() {
    val calculatorTest = CalculatorTest()
    calculatorTest.addTest()
}

class CalculatorTest {
    fun addTest() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.add(3)

        // then
        if (calculator.number != 8) {
            throw AssertionError("add 함수의 결과가 8이 아닙니다.")
        }
    }
}