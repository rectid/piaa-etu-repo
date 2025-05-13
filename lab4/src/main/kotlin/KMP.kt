object Logger {
    var enabled = true

    fun log(msg: String) {
        if (enabled) println(msg)
    }
}


fun computePrefixFunction(p: String): IntArray {
    val m = p.length
    val pi = IntArray(m)
    var k = 0
    Logger.log("Строим префикс-функцию для строки: \"$p\"")

    for (i in 1 until m) {
        Logger.log("i = $i, p[i] = '${p[i]}', k = $k")
        while (k > 0 && p[i] != p[k]) {
            Logger.log("  Несовпадение: '${p[i]}' != '${p[k]}', откат k -> pi[${k - 1}] = ${pi[k - 1]}")
            k = pi[k - 1]
        }
        if (p[i] == p[k]) {
            k++
            Logger.log("  Совпадение: '${p[i]}' == '${p[k - 1]}', увеличиваем k -> $k")
        }
        pi[i] = k
        Logger.log("  pi[$i] = $k")
    }

    Logger.log("Итоговая префикс-функция: ${pi.joinToString()}")
    return pi
}


fun kmpSearch(text: String, pattern: String): List<Int> {
    val combined = "$pattern#$text"
    Logger.log("Запуск KMP поиска подстроки \"$pattern\" в строке \"$text\"")
    Logger.log("Комбинированная строка для префикс-функции: \"$combined\"")

    val pi = computePrefixFunction(combined)
    val result = mutableListOf<Int>()
    val m = pattern.length

    for (i in m + 1 until combined.length) {
        Logger.log("Проверка позиции i = $i, pi[i] = ${pi[i]}")
        if (pi[i] == m) {
            val idx = i - 2 * m
            Logger.log("  Подстрока найдена! Начало в позиции $idx")
            result.add(idx)
        }
    }

    return result
}


fun main() {
    val pattern = readln()
    val text = readln()

    val positions = kmpSearch(text, pattern)
    if (positions.isEmpty()) {
        println(-1)
        return
    }
    println("Результат: ${positions.joinToString(",")}")
}
