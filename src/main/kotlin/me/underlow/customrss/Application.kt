package me.underlow.customrss

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.main() {
    routing {
        imdbRoute()
        drive2Route()
    }
}

private fun Routing.imdbRoute() {
    get("/imdb/rating/{userId}") {
        val userId = call.parameters["userId"]

        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val feed = Imdb.fetch(userId.replace(".com", ""))

        call.respondText(contentType = ContentType.Text.Xml) { writeFeedXml(feed) }
    }
}

private fun Routing.drive2Route() {
    get("/drive2/experience/{path...}") {
        val path = call.parameters.getAll("path")?.joinToString("/")

        if (path == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val feed = Drive2.fetch(path.replace(".com", ""))

        call.respondText(contentType = ContentType.Text.Xml) { writeFeedXml(feed) }
    }
}


inline fun <reified T : Any> loggerFor(): Logger =
    LoggerFactory.getLogger(T::class.java.name) ?: throw Throwable("Cannot access the logger library")
