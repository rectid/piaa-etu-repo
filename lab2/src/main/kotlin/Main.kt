package ru.rectid

import ApproximateTspStrategy
import BranchAndBoundTspStrategy
import TspStrategy
import java.io.File
import java.util.*

enum class ChosenStrategy {
    BNB,
    APPROX
}

fun main() {
    println("Выберите опцию:")
    println("1. Сгенерировать матрицу")
    println("2. Загрузить матрицу из файла")
    print("Ваш выбор: ")
    val choice = readln().toInt()

    val matrix = when (choice) {
        1 -> {
            print("Введите размер матрицы: ")
            val size = readln().toInt()
            print("Выберите тип матрицы (1 - симметричная, 2 - несимметричная): ")
            val symmetricInput = readln().toInt()
            val symmetric = symmetricInput == 1
            val matrix = generateMatrix(size, symmetric = symmetric)
            print("Введите имя файла для сохранения: ")
            val fileName = readln()
            saveMatrixToFile(matrix, fileName)
            println("Матрица сохранена в $fileName")
            matrix
        }

        2 -> {
            print("Введите имя файла: ")
            val fileName = readln()
            loadMatrixFromFile(fileName)
        }

        else -> throw IllegalArgumentException("Неверный выбор")
    }

    print("Введите стартовую вершину (0..${matrix.size - 1}): ")
    val startVertex = readln().toInt()

    val context = Context(matrix)

    println("\n=== BNB Strategy ===")
    context.strategy = ChosenStrategy.BNB
    context.run(startVertex)

    println("\n=== APPROX Strategy ===")
    context.strategy = ChosenStrategy.APPROX
    context.run(startVertex)
}


fun readMatrix(): Array<IntArray> {
    val input = mutableListOf<String>()
    while (true) {
        val line = readLine() ?: break
        if (line.trim().isEmpty()) break
        input.add(line)
    }

    return input.map { line ->
        line.split(" ").map { it.toInt() }.toIntArray()
    }.toTypedArray()
}

class Context(private val matrix: Array<IntArray>) {
    lateinit var strategy: ChosenStrategy

    fun run(startVertex: Int) {
        val solver: TspStrategy = when (strategy) {
            ChosenStrategy.BNB -> BranchAndBoundTspStrategy()
            ChosenStrategy.APPROX -> ApproximateTspStrategy()
        }

        printResult(solver.solve(matrix, startVertex))
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

fun generateMatrix(n: Int, maxWeight: Int = 100, symmetric: Boolean = true): Array<IntArray> {
    val rand = Random()
    val matrix = Array(n) { IntArray(n) { Int.MAX_VALUE } }

    for (i in 0 until n) {
        for (j in 0 until n) {
            if (i == j) continue
            matrix[i][j] = rand.nextInt(maxWeight) + 1
            if (symmetric) {
                matrix[j][i] = matrix[i][j]
            }
        }
    }

    return matrix
}


fun saveMatrixToFile(matrix: Array<IntArray>, fileName: String) {
    File(fileName).printWriter().use { out ->
        matrix.forEach { row ->
            out.println(row.joinToString(" ") { if (it == Int.MAX_VALUE) "INF" else it.toString() })
        }
    }
}

fun loadMatrixFromFile(fileName: String): Array<IntArray> {
    val lines = File(fileName).readLines()
    return lines.map { line ->
        line.trim().split(" ").map {
            if (it == "INF") Int.MAX_VALUE else it.toInt()
        }.toIntArray()
    }.toTypedArray()
}
