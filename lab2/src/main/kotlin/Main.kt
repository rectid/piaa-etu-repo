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
    val strategy = ChosenStrategy.BNB
    val matrix = readMatrix(numOfCities)

    val solver: TspStrategy = when (strategy) {
        ChosenStrategy.BNB -> BranchAndBoundTspStrategy()
        ChosenStrategy.APPROX -> ApproximateTspStrategy()
    }

    printResult(solver.solve(matrix))
}

fun printResult(result: Pair<Int, List<Int>>) {
    if (result.first == -1) {
        println("no path")
        return
    }
    println(result.second.joinToString(" "))
    print(result.first.toFloat())
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