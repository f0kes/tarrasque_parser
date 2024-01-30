package initialization

import initialization.CompositionRoot.Companion.instance
import services.Services.get
import services.runnerRegistry.RunnerRegistry
import skadistats.clarity.processor.runner.SimpleRunner
import skadistats.clarity.source.MappedFileSource


class Main {
    @Throws(Exception::class)
    fun run(args: Array<String>) {
        instance!!.initialize(args)
        val runnerRegistry = get<RunnerRegistry>()
        val tStart = System.currentTimeMillis()
        val s = MappedFileSource(args[0])
        val runner = SimpleRunner(s)
        runner.runWith(*runnerRegistry.getRunners().toTypedArray())
        val tMatch = System.currentTimeMillis() - tStart

        println("total time taken: $tMatch ms")
        benchmarks.Benchmark.dump()

        s.close()
    }


    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                Main().run(args)
            } catch (e: Exception) {
                Thread.sleep(1000)
                throw e
            }
        }
    }
}
