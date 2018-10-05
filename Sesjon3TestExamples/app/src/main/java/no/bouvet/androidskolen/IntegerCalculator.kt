package no.bouvet.androidskolen

object IntegerCalculator {

    fun add(augend: Int, addend: Int): Int {
        val sum = augend + addend
        return sum
    }

    fun subtract(minuend: Int, substrahend: Int): Int {
        val difference =  minuend - substrahend
        return difference
    }

    fun multiply(multiplicand: Int, multiplier: Int): Int {
        val product = multiplicand * multiplier
        return product
    }

    fun divide(dividend: Int, divisor: Int): Int {
        val quotient = (dividend.toDouble() / divisor).toInt()
        return quotient
    }


}
