package de.nosqlgeeks.readys.plugins

import de.nosqlgeeks.readys.service.person
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    //log is a function that comes from io.ktor.server.application
    log.info("Configuring routing ...")

    routing {
        hello()
        person()
    }
}
