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
import services.entityProvider.*
import components.GameComponent
import model.FullModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.plugin.RequestScope
import services.inputStreamProcessor.InputStreamProcessor
import services.demoInfoProvider.DemoInfoProvider
import services.entityProvider.HeroEntitiesProvider

val defaultComposition = module {

    single<IEntityMapper> { EntityMapper("localhost", 6379) }

    single { EntityPropertyGetter() }
    scope<RequestScope>
    {
        scopedOf(::RunnerRegistry)
        scopedOf(::FullModel)
        scopedOf(::InputStreamProcessor)
        scopedOf(::EntityUpdateProvider)
        scopedOf(::StringTableProvider)
        scopedOf(::Ticker) { bind<ITicker>() }
        scopedOf(::HeroEntitiesProviderByDTClass) { bind<HeroEntitiesProvider>() }
        scopedOf(::HeroComponentFactory)
        scopedOf(::GameComponent)
        scopedOf(::VisionTracker)
        scopedOf(::DtClassesProvider)
        scopedOf(::DemoInfoProvider)
    }
}

