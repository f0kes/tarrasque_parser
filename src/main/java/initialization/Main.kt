package initialization

import components.GameComponent
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.FullModel
import model.enums.Team
import org.koin.core.context.GlobalContext.get
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.scope
import services.demoInfoProvider.DemoInfoProvider
import services.entityMapper.EntityMapper
import services.entityMapper.IEntityMapper
import services.inputStreamProcessor.InputStreamProcessor
import java.io.InputStream


fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        install(Koin) {
            modules(defaultComposition)
        }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
                cause.printStackTrace()
            }
        }

        routing {
            post("/upload") {
                call.upload()
            }
            get("/health") {
                call.respondText("OK", status = HttpStatusCode.OK)
            }
            get("/getTables") {
                call.getTables()
            }
            get("/")
            {
                call.respondText("endpoints: /upload, /health, /getTables", status = HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}

suspend fun ApplicationCall.upload() {
    val inputStream = receiveStream()
    coroutineScope {
        val deferred = async(Dispatchers.IO) { runWithStream(inputStream) }
        deferred.await()
    }
    respond()
}

suspend fun ApplicationCall.getTables() {

    val entityMapper by inject<IEntityMapper>()
    val tables = entityMapper.exportAllTables()
    respond(
        HttpStatusCode.OK, tables
    )
}

suspend fun ApplicationCall.runWithStream(inputStream: InputStream) {
    val inputStreamProcessor = scope.get<InputStreamProcessor>()
    scope.get<GameComponent>()
    scope.get<DemoInfoProvider>()
    inputStreamProcessor.run(inputStream)
}

suspend fun ApplicationCall.respond() {
    val infoProvider = scope.get<DemoInfoProvider>()
    val winner = infoProvider.demoFileInfo?.gameInfo?.dota?.gameWinner
    val matchId = infoProvider.demoFileInfo?.gameInfo?.dota?.matchId
    val fullModel = scope.get<FullModel>()

    fullModel.winner = Team.fromInt(winner ?: -1)
    fullModel.matchId = matchId ?: -1

    val json = Json.encodeToString(fullModel)
    respond(
        HttpStatusCode.OK, json
    )
}

fun printStackTrace(cause: Throwable) {
    cause.printStackTrace()
}
