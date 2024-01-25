package services.dtClassesProvider

import events.Event
import events.EventListener
import lookups.PlayerResourceLookup
import runner.Runner
import services.runnerRegistry.RunnerRegistry
import skadistats.clarity.event.Insert
import skadistats.clarity.processor.sendtables.DTClasses
import skadistats.clarity.processor.sendtables.OnDTClassesComplete

class DtClassesProvider(runnerRegistry: RunnerRegistry) : Runner(runnerRegistry) {
    @Insert
    private val dtClasses: DTClasses? = null
    private val dtClassesCompleteEvent: Event<DTClasses> = Event()

    fun subscribe(listener: EventListener<DTClasses>) {
        dtClassesCompleteEvent.subscribe(listener)
    }

    @OnDTClassesComplete
    protected fun onDtClassesComplete() {
        dtClassesCompleteEvent.invoke(dtClasses!!)
    }

    fun dumpDtClasses() {
        for (dtClass in dtClasses!!.iterator()) {
            println(dtClass)
        }
    }


}