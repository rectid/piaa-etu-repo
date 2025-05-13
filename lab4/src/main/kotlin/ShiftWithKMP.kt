fun findCyclicShiftIndex(a: String, b: String): Int {
    val n = a.length
    Logger.log("Поиск циклического сдвига: A = \"$a\", B = \"$b\"")

    if (n != b.length) {
        Logger.log("  Длины строк не равны — невозможно")
        return -1
    }
    if (n == 0) {
        Logger.log("  Обе строки пустые — сдвиг = 0")
        return 0
    }

    val pi = computePrefixFunction(a)
    var j = 0

    for (i in 0 until 2 * n) {
        val ch = b[i % n]
        Logger.log("i = $i, символ из B = '$ch', сравниваем с A[$j] = '${if (j < n) a[j] else "-"}'")

        while (j > 0 && ch != a[j]) {
            Logger.log("  Несовпадение: '$ch' != '${a[j]}', откат j -> pi[${j - 1}] = ${pi[j - 1]}")
            j = pi[j - 1]
        }

        if (ch == a[j]) {
            j++
            Logger.log("  Совпадение: '$ch' == '${a[j - 1]}', j -> $j")
        }

        if (j == n) {
            val idxInBB = i - n + 1
            Logger.log("  Полное совпадение найдено в позиции $idxInBB в B+B")

            if (idxInBB < n) {
                val shift = (n - idxInBB) % n
                Logger.log("  Корректный сдвиг: $shift")
                return shift
            }

            Logger.log("  Совпадение за пределами первой половины B+B, продолжаем")
            j = pi[j - 1]
        }
    }

    Logger.log("Совпадений не найдено")
    return -1
}

fun main() {
    Logger.enabled = true

    val a = readln()
    val b = readln()

    val shift = findCyclicShiftIndex(a, b)
    println("Результат: $shift")
}
