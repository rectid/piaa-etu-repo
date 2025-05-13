fun main() {
    val s = readln()
    val p = readln()
    println(countComparisons(s, p))
}

fun countComparisons(s: String, p: String): Int {
    val n = s.length
    val m = p.length
    var count = 0

    for (i in 0..n - m) {
        for (j in 0 until m) {
            count++
            if (s[i+j] != p[j]) {
                break
            }
        }
    }

    return count
}
