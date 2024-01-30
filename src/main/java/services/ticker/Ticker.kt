package services.ticker

import events.Event
import events.EventListener
import runner.Runner
import services.entityPropertyGetter.getEntityProperty
import services.runnerRegistry.RunnerRegistry
import skadistats.clarity.event.Insert
import skadistats.clarity.processor.entities.Entities
import skadistats.clarity.processor.entities.UsesEntities
import skadistats.clarity.processor.reader.OnMessage
import skadistats.clarity.processor.reader.OnTickEnd
import skadistats.clarity.processor.reader.OnTickStart
import skadistats.clarity.wire.shared.common.proto.CommonNetworkBaseTypes

@UsesEntities
class Ticker(runnerRegistry: RunnerRegistry) : ITicker, Runner(runnerRegistry) {
    private val tickEvent = Event<Int>()
    private val secondEvent = Event<Int>()
    private var serverTick = 0
    private var lastSecondTickTime: Int = 0

    @Insert
    private val entities: Entities? = null
    override fun subscribeToTick(tickListener: EventListener<Int>) {
        tickEvent.subscribe(tickListener)
    }

    override fun unsubscribeFromTick(tickListener: EventListener<Int>) {
        tickEvent.unsubscribe(tickListener)
    }

    override fun subscribeToSecond(secondListener: EventListener<Int>) {
        secondEvent.subscribe(secondListener)
    }

    override fun unsubscribeFromSecond(secondListener: EventListener<Int>) {
        secondEvent.unsubscribe(secondListener)
    }

    private val time: Float
        get() {
            var time = 0f
            val grp = entities!!.getByDtName("CDOTAGamerulesProxy") ?: return 0f
            val oldTime = grp.getEntityProperty<Float>("m_pGameRules.m_fGameTime", null)
            if (oldTime == null) {
                // 7.32e on, need to calculate time manually
                val isPaused = grp.getEntityProperty<Boolean>("m_pGameRules.m_bGamePaused", null)!!
                val timeTick =
                    if (isPaused) grp.getEntityProperty<Int>("m_pGameRules.m_nPauseStartTick", null)!! else serverTick
                val pausedTicks = grp.getEntityProperty<Int>("m_pGameRules.m_nTotalPausedTicks", null)!!
                time = ((timeTick - pausedTicks).toFloat() / 30)
            } else {
                time = oldTime
            }
            return time
        }

    override fun getServerTime(): Int {
        return Math.round(time)
    }

    @OnMessage(CommonNetworkBaseTypes.CNETMsg_Tick::class)
    fun onMessage(message: CommonNetworkBaseTypes.CNETMsg_Tick) {
        serverTick = message.tick
//        tickEvent.invoke(serverTick)
//        if (serverTick % 30 == 0) {
//            secondEvent.invoke(serverTick / 30)
//        }
    }

    @OnTickEnd
    protected fun onTickEnd(synthetic: Boolean) { //TODO: check for pause
        tickEvent.invoke(serverTick)
        if (serverTick % 30 == 0 && time.toInt() > lastSecondTickTime) {
            secondEvent.invoke(time.toInt())
            lastSecondTickTime = time.toInt()
        }
    }

}