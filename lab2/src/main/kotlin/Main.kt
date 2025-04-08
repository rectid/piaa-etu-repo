package ru.rectid

enum class ChosenStrategy {
    DYNAMIC,
    DOUBLE_MST,
    BNB
}

fun main() {
    val numOfCities = readln().toInt()
    val strategy = ChosenStrategy.DYNAMIC
    val matrix = readMatrix(numOfCities)

}

fun readMatrix(numOfCities: Int) : Array<Array<Int>> {
    val matrix = Array(numOfCities) { Array(numOfCities) { 0 } }
    for (i in 0 until numOfCities) {
        val row = readln().split(" ").map { it.toInt() }
        for (j in 0 until numOfCities) {
            matrix[i][j] = row[j]
        }
    }
    return matrix
}