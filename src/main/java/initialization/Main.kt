package initialization

import initialization.CompositionRoot.Companion.instance
import lookups.HeroLookup
import lookups.PlayerResourceLookup
import services.Services
import services.Services.get
import services.dtClassesProvider.DtClassesProvider
import services.entityPropertyGetter.getEntityProperty
import services.entityUpdateProvider.EntityUpdate
import services.entityUpdateProvider.EntityUpdateProvider
import services.runnerRegistry.RunnerRegistry
import skadistats.clarity.ClarityException
import skadistats.clarity.model.DTClass
import skadistats.clarity.model.Entity
import skadistats.clarity.model.FieldPath
import skadistats.clarity.processor.runner.SimpleRunner
import skadistats.clarity.processor.sendtables.DTClasses
import skadistats.clarity.source.MappedFileSource


class Main {
    @Throws(Exception::class)
    fun run(args: Array<String>) {
        instance!!.initialize()
        val runnerRegistry = get<RunnerRegistry>()
        val provider = get<DtClassesProvider>()
        val entityUpdateProvider = get<EntityUpdateProvider>()

        var playerResouceDtClass: DTClass
        val tStart = System.currentTimeMillis()
        val s = MappedFileSource(args[0])
        val runner = SimpleRunner(s)
        runner.runWith(*runnerRegistry.getRunners().toTypedArray())
        val tMatch = System.currentTimeMillis() - tStart

        println("total time taken: $tMatch ms")
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
