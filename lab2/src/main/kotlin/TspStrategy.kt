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

        val mst = Array(n) { mutableListOf<Int>() }
        val visited = BooleanArray(n)
        val pq = PriorityQueue<Triple<Int, Int, Int>>(compareBy { it.third }) // from, to, weight
        visited[startVertex] = true

        for (v in 0 until n) {
            if (v != startVertex && matrix[startVertex][v] != Int.MAX_VALUE) {
                pq.add(Triple(startVertex, v, matrix[startVertex][v]))
            }
        }

        while (pq.isNotEmpty()) {
            val (from, to, _) = pq.poll()
            if (visited[to]) continue
            visited[to] = true
            mst[from].add(to)
            mst[to].add(from)

            for (v in 0 until n) {
                if (!visited[v] && matrix[to][v] != Int.MAX_VALUE) {
                    pq.add(Triple(to, v, matrix[to][v]))
                }
            }
        }

        val path = mutableListOf<Int>()
        val seen = BooleanArray(n)

        fun dfs(v: Int) {
            seen[v] = true
            path.add(v)
            for (u in mst[v]) {
                if (!seen[u]) dfs(u)
            }
        }

        dfs(startVertex)

        var totalCost = 0
        for (i in 0 until path.size - 1) {
            val cost = matrix[path[i]][path[i + 1]]
            if (cost == Int.MAX_VALUE) return -1 to emptyList()
            totalCost += cost
        }

        val returnCost = matrix[path.last()][startVertex]
        if (returnCost == Int.MAX_VALUE) return -1 to emptyList()
        totalCost += returnCost

        return totalCost to path
    }
}
