package de.nosqlgeeks.readys.plugins

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import java.text.DateFormat

fun Application.configureSerialization() {

    install(ContentNegotiation) {
        println("Installing serialization plugin ...")
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
}