import java.util.*

interface TspStrategy {
    fun solve(matrix: Array<IntArray>, startVertex: Int = 0): Pair<Int, List<Int>>
}

object Logger {
    var depth = 0
    var enabled = true

    fun log(message: String) {
        if (enabled) println("${"  ".repeat(depth)}$message")
    }

    inline fun <T> withIndent(block: () -> T): T {
        depth++
        val result = block()
        depth--
        return result
    }
}


class BranchAndBoundTspStrategy : TspStrategy {
    override fun solve(matrix: Array<IntArray>, startVertex: Int): Pair<Int, List<Int>> {
        val n = matrix.size
        if (n == 0) return -1 to emptyList()
        if (n == 1) return 0 to listOf(startVertex)

        var minCost = Int.MAX_VALUE
        var bestPath = listOf<Int>()

        fun calculateHalfSumMinEdges(visited: Set<Int>): Int {
            var sum = 0
            for (i in 0 until n) {
                if (i in visited && i != startVertex) continue
                val edges = matrix[i].filterIndexed { index, _ ->
                    index != i && (index !in visited || index == startVertex) && matrix[i][index] != Int.MAX_VALUE
                }.sorted()
                sum += when {
                    edges.isEmpty() -> return Int.MAX_VALUE
                    edges.size == 1 -> edges[0]
                    else -> (edges[0] + edges[1]) / 2
                }
            }
            return sum
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
                    if (v != vertex && (!visited.contains(v) || v == startVertex) && matrix[vertex][v] != Int.MAX_VALUE) {
                        pq.add(v to matrix[vertex][v])
                    }
                }
            }

            return if (inMST.size == n - visited.size + 1) weight else Int.MAX_VALUE
        }

        fun dfs(path: List<Int>, visited: Set<Int>, currentCost: Int) {
            val currentVertex = path.last()
            Logger.log("DFS at $path (cost: $currentCost)")

            if (path.size == n) {
                val returnCost = matrix[currentVertex][startVertex]
                if (returnCost != Int.MAX_VALUE) {
                    val totalCost = currentCost + returnCost
                    Logger.withIndent {
                        Logger.log("Complete path found: ${path + startVertex} with cost $totalCost")
                    }
                    if (totalCost < minCost) {
                        minCost = totalCost
                        bestPath = path + startVertex
                    }
                }
                return
            }

            val halfSumEstimate = calculateHalfSumMinEdges(visited)
            val mstEstimate = calculateMSTWeight(visited, currentVertex)
            val lowerBound = currentCost + maxOf(halfSumEstimate, mstEstimate)

            Logger.withIndent {
                Logger.log("Lower bound = $lowerBound, current minCost = $minCost")
            }

            if (lowerBound >= minCost) {
                Logger.withIndent {
                    Logger.log("Pruned branch (bound >= minCost)")
                }
                return
            }

            val neighbors = (0 until n).filter {
                it != currentVertex && it !in visited && matrix[currentVertex][it] != Int.MAX_VALUE
            }.sortedBy { matrix[currentVertex][it] }

            for (neighbor in neighbors) {
                val newCost = currentCost + matrix[currentVertex][neighbor]
                Logger.withIndent {
                    Logger.log("Go to $neighbor (edge: ${matrix[currentVertex][neighbor]}, newCost: $newCost)")
                }
                if (newCost < minCost) {
                    Logger.withIndent {
                        dfs(path + neighbor, visited + neighbor, newCost)
                    }
                }
            }
        }


        dfs(listOf(startVertex), setOf(startVertex), 0)

        return if (minCost == Int.MAX_VALUE) -1 to emptyList()
        else minCost to bestPath
    }
}

class ApproximateTspStrategy : TspStrategy {
    override fun solve(matrix: Array<IntArray>, startVertex: Int): Pair<Int, List<Int>> {
        val n = matrix.size
        if (n == 0) return -1 to emptyList()
        if (n == 1) return 0 to listOf(startVertex, startVertex)

        val path = mutableListOf<Int>()
        path.add(startVertex)

        for (i in 0 until n) {
            if (i != startVertex) path.add(i)
        }

        path.add(startVertex)

        var currentCost = calculatePathCost(matrix, path)
        var improved: Boolean
        var iterations = 0
        val maxIterations = n * n

        Logger.log("Initial path: $path")
        Logger.log("Initial cost: $currentCost")

        do {
            improved = false
            Logger.log("Iteration $iterations")
            Logger.withIndent {
                for (i in 1 until n) {
                    for (j in i + 1 until n) {
                        val newPath = path.toMutableList()
                        newPath[i] = path[j].also { newPath[j] = path[i] }

                        val newCost = calculatePathCost(matrix, newPath)
                        Logger.log("Trying swap ($i, $j): cost = $newCost")

                        if (newCost < currentCost) {
                            Logger.log("Improved! Previous cost: $currentCost, New cost: $newCost")
                            path.clear()
                            path.addAll(newPath)
                            currentCost = newCost
                            improved = true
                        }
                    }
                }
            }
            iterations++
        } while (improved && iterations < maxIterations)

        Logger.log("Final path: $path")
        Logger.log("Final cost: $currentCost")


        return currentCost to path
    }

    private fun calculatePathCost(matrix: Array<IntArray>, path: List<Int>): Int {
        var cost = 0
        for (i in 0 until path.size - 1) {
            val from = path[i]
            val to = path[i + 1]
            if (matrix[from][to] == Int.MAX_VALUE) return Int.MAX_VALUE
            cost += matrix[from][to]
        }
        return cost
    }
}