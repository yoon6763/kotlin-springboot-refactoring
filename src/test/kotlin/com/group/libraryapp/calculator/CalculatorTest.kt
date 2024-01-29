package com.group.libraryapp.calculator

import org.hibernate.boot.model.naming.IllegalIdentifierException

fun main() {
    val calculatorTest = CalculatorTest()
    calculatorTest.addTest()
    calculatorTest.minusTest()
    calculatorTest.multiplyTest()
    calculatorTest.divideTest()
    calculatorTest.divideExceptionTest()
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

    fun minusTest() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.minus(3)

        // then
        if (calculator.number != 2) {
            throw AssertionError("minus 함수의 결과가 2가 아닙니다.")
        }
    }

    fun multiplyTest() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.multiply(3)

        // then
        if (calculator.number != 15) {
            throw AssertionError("multiply 함수의 결과가 15가 아닙니다.")
        }
    }

    fun divideTest() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.divide(2)

        // then
        if (calculator.number != 2) {
            throw AssertionError("divide 함수의 결과가 1이 아닙니다.")
        }
    }

    fun divideExceptionTest() {
        // given
        val calculator = Calculator(5)

        // when
        try {
            calculator.divide(0)
        } catch (e: IllegalArgumentException) {
            return
        } catch (e: Exception) {
            throw IllegalIdentifierException("divide 함수에 0을 넣었을 때 IllegalArgumentException이 발생해야 합니다.")
        }

        throw IllegalIdentifierException("divide 함수에 0을 넣었을 때 IllegalArgumentException이 발생해야 합니다.")
    }
}