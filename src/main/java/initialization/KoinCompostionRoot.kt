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
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.scopedOf
import org.koin.ktor.plugin.RequestScope
import services.inputStreamProcessor.InputStreamProcessor
import skadistats.clarity.Clarity

val defaultComposition = module {

    single<IEntityMapper> { EntityMapper("localhost", 6379) }
    single { EntityPropertyGetter() }
    scope<RequestScope>
    {
        scopedOf(::RunnerRegistry)
        scopedOf(::InputStreamProcessor)
        scopedOf(::EntityUpdateProvider)
        scopedOf(::StringTableProvider)
        scopedOf(::Ticker) { bind<ITicker>() }
        scopedOf(::HeroEntitiesProvider)
        scopedOf(::HeroComponentFactory)
        scopedOf(::GameComponent)
        scopedOf(::VisionTracker)
        scopedOf(::DtClassesProvider)
    }
}

