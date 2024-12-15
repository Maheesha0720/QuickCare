import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

data class AmbulanceRequest(val latitude: Double, val longitude: Double, val additionalInfo: String)

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson { setPrettyPrinting() }
        }
        routing {
            post("/request-ambulance") {
                val request = call.receive<AmbulanceRequest>()
                // Handle the ambulance request, e.g., notify a dispatcher
                println("Received ambulance request: $request")
                call.respond("Ambulance request received")
            }
        }
    }
    server.start(wait = true)
}
