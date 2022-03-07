package de.nosqlgeeks.readys

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import de.nosqlgeeks.readys.plugins.*
import de.nosqlgeeks.readys.plugins.configureRouting

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
    }.start(wait = true)
}
