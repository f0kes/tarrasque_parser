package benchmarks

object Benchmark {
    private val benchmarkMap = mutableMapOf<Any, BenchmarkData>()
    fun start(key: Any): Any {
        val data = benchmarkMap.computeIfAbsent(key) { BenchmarkData(key.toString()) }
        data.startTime = System.currentTimeMillis()
        data.count++
        return key
    }

    fun stop(key: Any) {
        val data = benchmarkMap[key] ?: return
        data.totalTime += System.currentTimeMillis() - data.startTime
    }

    fun dump() {
        val sorted = benchmarkMap.values.sortedByDescending { it.totalTime }
        for (data in sorted) {
            println("${data.name}: ${data.totalTime} ms, ${data.count} times")
        }
    }

    private data class BenchmarkData(
        var name: String,
        var startTime: Long = 0,
        var totalTime: Long = 0,
        var count: Int = 0
    )
}