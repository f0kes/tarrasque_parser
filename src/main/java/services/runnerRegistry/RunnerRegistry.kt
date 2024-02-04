package services.runnerRegistry

class RunnerRegistry() {
    private val runners: MutableList<Any> = ArrayList()

    fun register(runner: Any) {
        runners.add(runner)
    }

    fun unregister(runner: Any) {
        runners.remove(runner)
    }

    fun getRunners(): List<Any> {
        return runners
    }
}
