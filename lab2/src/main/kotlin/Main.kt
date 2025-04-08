package ru.rectid

import ApproximateTspStrategy
import BranchAndBoundTspStrategy
import TspStrategy

enum class ChosenStrategy {
    BNB,
    APPROX
}

fun main() {

    val numOfCities = readln().toInt()
    val context = Context(readMatrix(numOfCities))
    context.strategy = ChosenStrategy.BNB
    context.run()
    context.strategy = ChosenStrategy.APPROX
    context.run()
}

fun readMatrix(numOfCities: Int): Array<IntArray> {
    val matrix = Array(numOfCities) { IntArray(numOfCities) { 0 } }

    for (i in 0 until numOfCities) {
        val row = readln().split(" ").map { it.toInt() }
        for (j in 0 until numOfCities) {
            matrix[i][j] = row[j]
        }
    }

    return matrix
}

class Context(private val matrix: Array<IntArray>) {

    lateinit var strategy: ChosenStrategy

    fun run() {
        val solver: TspStrategy = when (strategy) {
            ChosenStrategy.BNB -> BranchAndBoundTspStrategy()
            ChosenStrategy.APPROX -> ApproximateTspStrategy()
        }

        printResult(solver.solve(matrix))
    }

    private fun printResult(result: Pair<Int, List<Int>>) {
        when (strategy) {
            ChosenStrategy.BNB -> println("BNB Strategy:")
            ChosenStrategy.APPROX -> println("APPROX Strategy:")
        }
        if (result.first == -1) {
            println("no path")
            return
        }
        println(result.second.joinToString(" "))
        println(result.first.toFloat())
    }
}

