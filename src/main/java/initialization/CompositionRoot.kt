//package initialization
//
//import components.GameComponent
//import model.enums.Team
//import services.Services
//import services.Services.get
//import services.Services.register
//import services.dtClassesProvider.DtClassesProvider
//import services.entityMapper.EntityMapper
//import services.entityMapper.IEntityMapper
//import services.entityPropertyGetter.EntityPropertyGetter
//import services.entityProvider.HeroEntitiesProvider
//import services.entityUpdateProvider.EntityUpdateProvider
//import services.heroComponentFactory.HeroComponentFactory
//import services.runnerRegistry.RunnerRegistry
//import services.stringTableProvider.StringTableProvider
//import services.ticker.ITicker
//import services.ticker.Ticker
//import services.visionTracker.VisionTracker
//import skadistats.clarity.Clarity
//
//class CompositionRoot private constructor() {
//    private var initialized = false
//
//    fun initialize(args: Array<String>) {
//        if (initialized) {
//            throw IllegalStateException("CompositionRoot is already initialized")
//        }
//        val runnerRegistry = register<RunnerRegistry>(RunnerRegistry(), true)
//        val entityMapper = register<IEntityMapper>(EntityMapper("localhost", 6379), true)
//        register<EntityUpdateProvider>(EntityUpdateProvider(runnerRegistry), true)
//        register<DtClassesProvider>(DtClassesProvider(runnerRegistry), true)
//        register<EntityPropertyGetter>(EntityPropertyGetter(), true)
//        val stringTableProvider = register<StringTableProvider>(StringTableProvider(runnerRegistry, entityMapper), true)
//        register<ITicker>(Ticker(runnerRegistry), true)
//        val heroComponentFactory = HeroComponentFactory(HeroEntitiesProvider(get()))
//        //val winner = getWinner(args[0])
//        //todo: winner is set to radiant for now
//        register<GameComponent>(GameComponent(heroComponentFactory, get(), get(), get(), get()), true)
//        register<VisionTracker>(VisionTracker(heroComponentFactory, get()), true)
//
//        initialized = true
//    }
//
//    private fun getWinner(file: String): Team {
//        val info = Clarity.infoForFile(file)
//        val winnerInt = info.gameInfo.dota.gameWinner
//        val winner = Team.fromInt(winnerInt)
//        return winner
//    }
//
//    fun dispose() {
//        if (!initialized) {
//            throw IllegalStateException("CompositionRoot is not initialized")
//        }
//        Services.dispose()
//        initialized = false
//    }
//
//    companion object {
//        @kotlin.jvm.JvmStatic
//        var instance: CompositionRoot? = null
//            get() {
//                if (field == null) {
//                    field = CompositionRoot()
//                }
//                return field
//            }
//            private set
//    }
//}
