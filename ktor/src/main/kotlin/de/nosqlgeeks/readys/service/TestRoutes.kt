package de.nosqlgeeks.readys.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.index() {
    get("/") {
        call.respondText("Hello World!")
    }
}