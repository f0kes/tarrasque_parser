package initialization

import org.koin.dsl.module
import services.dtClassesProvider.DtClassesProvider
import services.entityMapper.EntityMapper
import services.entityMapper.IEntityMapper
import services.entityPropertyGetter.EntityPropertyGetter
import services.entityUpdateProvider.EntityUpdateProvider
import services.runnerRegistry.RunnerRegistry
import services.stringTableProvider.StringTableProvider
import services.ticker.ITicker
import services.ticker.Ticker
import services.visionTracker.VisionTracker
import services.heroComponentFactory.HeroComponentFactory
import services.entityProvider.HeroEntitiesProvider
import components.GameComponent
import model.enums.Team
import skadistats.clarity.Clarity

val myModule = module {
    single { params -> RunnerRegistry(params[0]) }
    single<IEntityMapper> { EntityMapper("localhost", 6379) }
    single { EntityUpdateProvider(get()) }
    single { DtClassesProvider(get()) }
    single { EntityPropertyGetter() }
    single { StringTableProvider(get(), get()) }
    single<ITicker> { Ticker(get()) }
    single { HeroComponentFactory(HeroEntitiesProvider()) }
    single { GameComponent(get(), getWinner(get()), get()) }
    single { VisionTracker(get()) }
    single { params -> getWinner(params[0]) }
}

fun getWinner(file: String): Team {
    val info = Clarity.infoForFile(file)
    val winnerInt = info.gameInfo.dota.gameWinner
    return Team.fromInt(winnerInt)
}

class KoinCompostionRoot {
}