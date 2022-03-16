package de.nosqlgeeks.readys.plugins

import de.nosqlgeeks.readys.service.person
import de.nosqlgeeks.readys.service.post
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {

    //log is a function that comes from io.ktor.server.application
    log.info("Configuring routing ...")

    routing {
        index()
        person()
        post()
        //TODO: stats()
    }
}
