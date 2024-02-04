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
import kotlinx.coroutines.withContext
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.scope
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
                println("Uploading file...")
                val inputStream = call.receiveStream()
                coroutineScope {
                    val deferred = async(Dispatchers.IO) { call.uploadStream(inputStream) }
                    deferred.await()
                }

                call.respondText("File uploaded successfully", status = HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}

suspend fun ApplicationCall.uploadStream(inputStream: InputStream) {
    val inputStreamProcessor = scope.get<InputStreamProcessor>()
    scope.get<GameComponent>()
    inputStreamProcessor.run(inputStream)
}

fun printStackTrace(cause: Throwable) {
    cause.printStackTrace()
}
