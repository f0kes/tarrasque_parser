package services.inputStreamProcessor

import model.enums.Team
import services.runnerRegistry.RunnerRegistry
import skadistats.clarity.Clarity
import skadistats.clarity.processor.runner.SimpleRunner
import skadistats.clarity.source.InputStreamSource
import java.io.InputStream

class InputStreamProcessor(private val runnerRegistry: RunnerRegistry) {
    private var inputStream: InputStream? = null
    private var winner: Team = Team.NEUTRAL
    fun run(instr: InputStream) {
        inputStream = instr
        val runner = SimpleRunner(InputStreamSource(instr))
        val info = Clarity.infoForStream(instr)
        val winnerInt = info.gameInfo.dota.gameWinner
        winner = Team.fromInt(winnerInt)
        runner.runWith(*runnerRegistry.getRunners().toTypedArray())
    }

    fun getInputStream(): InputStream? {
        return inputStream
    }

    fun getWinner(): Team {
        return winner
    }
}