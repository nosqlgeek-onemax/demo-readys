package de.nosqlgeeks.readys.plugins

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.model.Post
import de.nosqlgeeks.readys.data.serialize.PersonDeSerializer
import de.nosqlgeeks.readys.data.serialize.PersonSerializer
import de.nosqlgeeks.readys.data.serialize.PostDeSerializer
import de.nosqlgeeks.readys.data.serialize.PostSerializer
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import java.text.DateFormat

fun Application.configureSerialization() {

    install(ContentNegotiation) {
        println("Installing serialization plugin ...")

        gson {
            registerTypeAdapter(Person::class.java, PersonSerializer())
            registerTypeAdapter(Person::class.java, PersonDeSerializer())
            registerTypeAdapter(Post::class.java, PostSerializer())
            registerTypeAdapter(Post::class.java, PostDeSerializer())
            setPrettyPrinting()
        }
    }
}