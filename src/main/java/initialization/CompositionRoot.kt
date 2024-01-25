package initialization

import services.Services.register
import services.dtClassesProvider.DtClassesProvider
import services.entityMapper.EntityMapper
import services.entityMapper.IEntityMapper
import services.entityPropertyGetter.EntityPropertyGetter
import services.entityProvider.HeroEntitiesProvider
import services.entityUpdateProvider.EntityUpdateProvider
import services.heroComponentFactory.HeroComponentFactory
import services.runnerRegistry.RunnerRegistry
import services.stringTableProvider.StringTableProvider
import services.ticker.ITicker
import services.ticker.Ticker

class CompositionRoot private constructor() {
    private val initialized = false

    fun initialize() {

        val runnerRegistry = register<RunnerRegistry>(RunnerRegistry(), true)
        val entityMapper = register<IEntityMapper>(EntityMapper("localhost", 6379), true)
        val updateProvider = register<EntityUpdateProvider>(EntityUpdateProvider(runnerRegistry), true)
        val dtClassesProvider = register<DtClassesProvider>(DtClassesProvider(runnerRegistry), true)
        val propertyGetter = register<EntityPropertyGetter>(EntityPropertyGetter(), true)
        val stringTableProvider = register<StringTableProvider>(StringTableProvider(runnerRegistry, entityMapper), true)

        val ticker = register<ITicker>(Ticker(runnerRegistry), true)
        val heroComponentFactory = HeroComponentFactory(HeroEntitiesProvider())


        ticker.subscribeToSecond { tick: Int -> (heroComponentFactory.retrieveHeroComponents()) }
        //ticker.subscribeToSecond { tick: Int -> (dtClassesProvider.dumpDtClasses()) }
    }

    companion object {
        @kotlin.jvm.JvmStatic
        var instance: CompositionRoot? = null
            get() {
                if (field == null) {
                    field = CompositionRoot()
                }
                return field
            }
            private set
    }
}
