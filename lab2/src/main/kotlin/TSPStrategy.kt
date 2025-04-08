package ru.rectid

sealed interface TSPStrategy {
    fun solve()
}

sealed class DynamicProgrammingTSP : TSPStrategy {
    override fun solve() {
        TODO()
    }
}