import java.util.*

interface TspStrategy {
    fun solve(matrix: Array<IntArray>): Pair<Int, List<Int>>
}

class BranchAndBoundTspStrategy : TspStrategy {
    override fun solve(matrix: Array<IntArray>): Pair<Int, List<Int>> {
        val n = matrix.size
        if (n == 0) return -1 to emptyList()
        if (n == 1) return 0 to listOf(0)

        var minCost = Int.MAX_VALUE
        var bestPath = listOf<Int>()
        val startVertex = 0

        fun calculateHalfSumMinEdges(visited: Set<Int>): Int {
            var sum = 0
            for (i in 0 until n) {
                if (i in visited) continue
                val edges = matrix[i].filterIndexed { index, value ->
                    index != i && !visited.contains(index) && value != Int.MAX_VALUE
                }.sorted()
                sum += when {
                    edges.isEmpty() -> return Int.MAX_VALUE
                    edges.size == 1 -> edges[0]
                    else -> (edges[0] + edges[1])
                }
            }
            return (sum + 1) / 2
        }

        fun calculateMSTWeight(visited: Set<Int>, currentVertex: Int): Int {
            if (visited.size == n) return 0

            val pq = PriorityQueue<Pair<Int, Int>>(compareBy { it.second })
            val inMST = mutableSetOf<Int>()
            var weight = 0

            pq.add(currentVertex to 0)

            while (inMST.size < n - visited.size + 1 && pq.isNotEmpty()) {
                val (vertex, edgeWeight) = pq.poll()
                if (vertex in inMST) continue

                inMST.add(vertex)
                weight += edgeWeight

                for (v in 0 until n) {
                    if (v != vertex && !visited.contains(v) && matrix[vertex][v] != Int.MAX_VALUE) {
                        pq.add(v to matrix[vertex][v])
                    }
                }
            }

            return if (inMST.size == n - visited.size + 1) weight else Int.MAX_VALUE
        }

        fun dfs(path: List<Int>, visited: Set<Int>, currentCost: Int) {
            val currentVertex = path.last()

            if (path.size == n) {
                val returnCost = matrix[currentVertex][startVertex]
                if (returnCost != Int.MAX_VALUE) {
                    val totalCost = currentCost + returnCost
                    if (totalCost < minCost) {
                        minCost = totalCost
                        bestPath = path
                    }
                }
                return
            }

            val halfSumEstimate = calculateHalfSumMinEdges(visited)
            val mstEstimate = calculateMSTWeight(visited, currentVertex)

            val lowerBound = currentCost + maxOf(halfSumEstimate, mstEstimate)

            if (lowerBound >= minCost) return

            val neighbors = (0 until n)
                .filter { it != currentVertex && !visited.contains(it) && matrix[currentVertex][it] != Int.MAX_VALUE }
                .sortedBy { matrix[currentVertex][it] }

            for (neighbor in neighbors) {
                val newCost = currentCost + matrix[currentVertex][neighbor]
                if (newCost < minCost) {
                    dfs(path + neighbor, visited + neighbor, newCost)
                }
            }
        }

        dfs(listOf(startVertex), setOf(startVertex), 0)

        return if (minCost == Int.MAX_VALUE) -1 to emptyList()
        else minCost to bestPath
    }
}

class ApproximateTspStrategy : TspStrategy {
    override fun solve(matrix: Array<IntArray>): Pair<Int, List<Int>> {
        val n = matrix.size
        if (n == 0) return -1 to emptyList()
        if (n == 1) return 0 to listOf(0)

        val startVertex = 0
        val visited = mutableSetOf(startVertex)
        val path = mutableListOf(startVertex)
        var currentVertex = startVertex
        var totalCost = 0

        while (visited.size < n) {
            var nextVertex = -1
            var minEdge = Int.MAX_VALUE

            for (i in 0 until n) {
                if (i != currentVertex && !visited.contains(i) && matrix[currentVertex][i] < minEdge) {
                    minEdge = matrix[currentVertex][i]
                    nextVertex = i
                }
            }

            if (nextVertex == -1) break

            totalCost += minEdge
            path.add(nextVertex)
            visited.add(nextVertex)
            currentVertex = nextVertex
        }

        if (visited.size != n) return -1 to emptyList()

        val returnCost = matrix[currentVertex][startVertex]
        if (returnCost == Int.MAX_VALUE) return -1 to emptyList()

        totalCost += returnCost

        return totalCost to path
    }
}