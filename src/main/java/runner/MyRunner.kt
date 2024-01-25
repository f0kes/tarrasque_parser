package runner

import lookups.HeroLookup
import lookups.PlayerResourceLookup
import skadistats.clarity.ClarityException
import skadistats.clarity.event.Insert
import skadistats.clarity.io.Util
import skadistats.clarity.model.DTClass
import skadistats.clarity.model.Entity
import skadistats.clarity.model.FieldPath
import skadistats.clarity.processor.entities.Entities
import skadistats.clarity.processor.entities.OnEntityUpdated
import skadistats.clarity.processor.entities.UsesEntities
import skadistats.clarity.processor.reader.OnMessage
import skadistats.clarity.processor.reader.OnTickEnd
import skadistats.clarity.processor.resources.Resources
import skadistats.clarity.processor.resources.UsesResources
import skadistats.clarity.processor.runner.SimpleRunner
import skadistats.clarity.processor.sendtables.DTClasses
import skadistats.clarity.processor.sendtables.OnDTClassesComplete
import skadistats.clarity.processor.stringtables.StringTables
import skadistats.clarity.processor.stringtables.UsesStringTable
import skadistats.clarity.source.MappedFileSource
import skadistats.clarity.wire.shared.common.proto.CommonNetworkBaseTypes.CNETMsg_Tick
import java.util.function.Consumer

@UsesResources
@UsesEntities
@UsesStringTable("*")
class MyRunner {
    //private final Logger log = LoggerFactory.getLogger(Runner.class.getPackage().getClass());
    @Insert
    private val dtClasses: DTClasses? = null

    @Insert
    private val entities: Entities? = null

    @Insert
    private val resources: Resources? = null

    @Insert
    private val stringTables: StringTables? = null

    private var playerResourceClass: DTClass? = null
    private val playerLookup = arrayOfNulls<PlayerResourceLookup>(10)
    private val deferredActions: MutableList<Runnable> = ArrayList()

    private val uniqueDtClasses = HashSet<DTClass>()
    private val heroLookup = arrayOfNulls<HeroLookup>(10)
    private var serverTick = 0

    @Throws(Exception::class)
    fun run(args: Array<String>) {
        val tStart = System.currentTimeMillis()
        val s = MappedFileSource(args[0])
        val runner = SimpleRunner(s)
        runner.runWith(this)
        val tMatch = System.currentTimeMillis() - tStart

        //log.info("total time taken: {}s", (tMatch) / 1000.0);
        s.close()
    }

    @OnTickEnd
    protected fun onTickEnd(synthetic: Boolean) {
        deferredActions.forEach(Consumer { obj: Runnable -> obj.run() })
        deferredActions.clear()
    }

    @OnDTClassesComplete
    protected fun onDtClassesComplete() {
        playerResourceClass = dtClasses!!.forDtName("CDOTA_PlayerResource")
        for (i in 0..9) {
            playerLookup[i] = PlayerResourceLookup(playerResourceClass!!, i)
        }
    }

    @OnMessage(CNETMsg_Tick::class)
    fun onMessage(message: CNETMsg_Tick) {
        serverTick = message.tick
    }


    @OnEntityUpdated
    protected fun onEntityUpdated(e: Entity, changedFieldPaths: Array<FieldPath>, nChangedFieldPaths: Int) {
        val dtClass = e.dtClass

        if (dtClass === playerResourceClass) {
            updateHeroAssignments(e, changedFieldPaths, nChangedFieldPaths)
        } else {
            for (i in 0..9) {
                val lookup = heroLookup[i] ?: continue
                if (lookup.areItemsChanged(e, changedFieldPaths, nChangedFieldPaths)) {
                    val items = lookup.GetItems(stringTables!!.forName("EntityNames"))
                    // System.out.format("Player %02d changed items to %s, time:%d\n", i, String.join(", ", items), getTime());
                }
            }
        }
    }

    private val time: Int
        get() {
            var time = 0
            val grp = entities!!.getByDtName("CDOTAGamerulesProxy")
            val oldTime = getEntityProperty<Float>(grp, "m_pGameRules.m_fGameTime", null)
            if (oldTime == null) {
                // 7.32e on, need to calculate time manually
                val isPaused = getEntityProperty<Boolean>(grp, "m_pGameRules.m_bGamePaused", null)!!
                val timeTick = if (isPaused) getEntityProperty<Int>(grp, "m_pGameRules.m_nPauseStartTick", null)!! else serverTick
                val pausedTicks = getEntityProperty<Int>(grp, "m_pGameRules.m_nTotalPausedTicks", null)!!
                time = Math.round((timeTick - pausedTicks).toFloat() / 30)
            } else {
                time = Math.round(oldTime)
            }
            return time
        }

    fun <T> getEntityProperty(e: Entity?, property: String, idx: Int?): T? {
        var property = property
        try {
            if (e == null) {
                return null
            }
            if (idx != null) {
                property = property.replace("%i", Util.arrayIdxToString(idx))
            }
            val fp = e.dtClass.getFieldPathForName(property)
            return e.getPropertyForFieldPath(fp)
        } catch (ex: Exception) {
            return null
        }
    }

    private fun updateHeroAssignments(e: Entity, changedFieldPaths: Array<FieldPath>, nChangedFieldPaths: Int) {
        if (e.dtClass !== playerResourceClass) {
            throw ClarityException("expected entity of class %s, got %s", playerResourceClass!!.dtName, e.dtClass.dtName)
        }
        for (p in 0..9) {
            val lookup = playerLookup[p]
            if (lookup!!.isSelectedHeroChanged(e, changedFieldPaths, nChangedFieldPaths)) {
                val playerIndex = p
                deferredActions.add(Runnable {
                    val heroHandle = lookup.getSelectedHeroHandle(e)
                    val heroEntity = entities!!.getByHandle(heroHandle)

                    heroLookup[playerIndex] = HeroLookup(heroEntity, entities)
                })
            }
        }
    }


    private fun printEntityStringTable() {
        val entities = stringTables!!.forName("EntityNames")
        println(entities.toString())
    }

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                MyRunner().run(args)
            } catch (e: Exception) {
                Thread.sleep(1000)
                throw e
            }
        }
    }
}
