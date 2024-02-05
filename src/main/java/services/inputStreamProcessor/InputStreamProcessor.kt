package services.inputStreamProcessor

import model.enums.Team
import services.runnerRegistry.RunnerRegistry
import skadistats.clarity.Clarity
import skadistats.clarity.processor.runner.SimpleRunner
import skadistats.clarity.source.InputStreamSource
import java.io.BufferedInputStream
import java.io.InputStream

class InputStreamProcessor(private val runnerRegistry: RunnerRegistry) {
    private var inputStream: BufferedInputStream? = null
    private var winner: Team = Team.NEUTRAL
    private var matchId: Long = 0
    fun run(instr: InputStream) {
        inputStream = BufferedInputStream(instr)
        val runner = SimpleRunner(InputStreamSource(inputStream))
        runner.runWith(*runnerRegistry.getRunners().toTypedArray())
    }

    fun getInputStream(): InputStream? {
        return inputStream
    }

    fun getWinner(): Team {
        return winner
    }

    fun getMatchId(): Long {
        return matchId
    }
}