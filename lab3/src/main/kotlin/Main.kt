package ru.rectid

import kotlin.math.min

const val DEBUG = true

object Logger {
    fun log(message: String) {
        if (DEBUG) {
            println(message)
        }
    }
}

fun main() {
    val prices = readln().split(" ").map(String::toInt)
    val insertCost = prices[1]
    val deleteCost = prices[2]
    val replaceCost = prices[0]
    val replaceToTwoCost = prices[3]

    val s1 = readln()
    val s2 = readln()

    val dp = getLevenshteinDistance(s1, s2, insertCost, deleteCost, replaceCost, replaceToTwoCost)
    Logger.log("Расстояние Левенштейна: ${dp[s1.length][s2.length]}")


    println(getPrescription(s1, s2, dp, insertCost, deleteCost, replaceCost, replaceToTwoCost))
    println(s1)
    println(s2)
}

fun getLevenshteinDistance(s1: String, s2: String, insertCost: Int, deleteCost: Int, replaceCost: Int, replaceToTwoCost: Int): Array<IntArray> {
    val dp = Array(s1.length + 1) { IntArray(s2.length + 1) { 0 } }

    for (i in 1..s2.length) {
        dp[0][i] = dp[0][i - 1] + insertCost
        Logger.log("dp[0][$i] = ${dp[0][i]} (вставка $insertCost)")
    }
    for (i in 1..s1.length) {
        dp[i][0] = dp[i - 1][0] + deleteCost
        Logger.log("dp[$i][0] = ${dp[i][0]} (удаление $deleteCost)")
    }

    for (i in 1..s1.length) {
        for (j in 1..s2.length) {
            val costReplace = if (s1[i - 1] == s2[j - 1]) 0 else replaceCost
            dp[i][j] = min(
                dp[i - 1][j] + deleteCost,
                min(
                    dp[i][j - 1] + insertCost,
                    dp[i - 1][j - 1] + costReplace
                )
            )

            if (j >= 2) {
                dp[i][j] = min(dp[i][j], dp[i - 1][j - 2] + replaceToTwoCost)
            }

            Logger.log("dp[$i][$j] = ${dp[i][j]} (замена ${if (costReplace == 0) "нет" else replaceCost}, вставка $insertCost, удаление $deleteCost, замена на два $replaceToTwoCost)")
        }
    }

    return dp
}

fun getPrescription(s1: String, s2: String, dp: Array<IntArray>, insertCost: Int, deleteCost: Int, replaceCost: Int, replaceToTwoCost: Int): String {
    val sb = StringBuilder()
    var i = s1.length
    var j = s2.length

    while (i > 0 || j > 0) {
        when {
            i > 0 && j > 0 && s1[i - 1] == s2[j - 1] -> {
                sb.append("M")
                Logger.log("M (совпадение: s1[${i - 1}] = s2[${j - 1}])")
                i--
                j--
            }
            i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + replaceCost -> {
                sb.append("R")
                Logger.log("R (замена: s1[${i - 1}] = s2[${j - 1}])")
                i--
                j--
            }
            i > 0 && dp[i][j] == dp[i - 1][j] + deleteCost -> {
                sb.append("D")
                Logger.log("D (удаление: s1[${i - 1}])")
                i--
            }
            j > 0 && dp[i][j] == dp[i][j - 1] + insertCost -> {
                sb.append("I")
                Logger.log("I (вставка: s2[${j - 1}])")
                j--
            }
            i > 0 && j >= 2 && dp[i][j] == dp[i - 1][j - 2] + replaceToTwoCost -> {
                sb.append("T")
                Logger.log("T (замена одного символа на два: s1[${i - 1}] на два символа в s2)")
                i--
                j -= 2
            }
        }
    }

    return sb.reverse().toString()
}
