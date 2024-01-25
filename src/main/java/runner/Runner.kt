package runner

import services.runnerRegistry.RunnerRegistry

open class Runner(runnerRegistry: RunnerRegistry) {
    init {
        runnerRegistry.register(this)
    }
}