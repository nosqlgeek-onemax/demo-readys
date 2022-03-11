package de.nosqlgeeks.readys

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import de.nosqlgeeks.readys.plugins.configureRouting
import de.nosqlgeeks.readys.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.*


/**
 * ## Learning Index
 *
 * ### Kotlin
 * 0.) Application.kt : Project setup
 * I.) Person.kt : Data classes + Static typing + Mutable types + Default values
 * II.) Post.kt : Companion objects + Non-nullable types
 * III.) GsonFactory.kt : Any + Portability
 * IV.) IRepo.kt : Interfaces + Inheritance
 * V.) PersonTest.kt : Testing + Lambdas
 * VI.) Repo.kt : Type casting
 *
 *  ## RediSearch
 *
 *  I.) Repo.kt - Creating an index + JSON support
 *  II.) Person*(De)Serializer - (De)Serialization basics
 *  III.) TODO
 *  
 *
 * ## 0.) Let's learn some Kotlin
 *
 * 1. This is more than just a Kotlin App. It is more specifically a 'ktor' application. 'ktor' is Kotlin's web
 *    application framework. We will cover more about it later.
 * 2. Our application pulls the necessary dependencies in via Maven. We could have used 'Gradle Kotlin', 'Gradle Groovy',
 *    or 'Maven'. The only reason why I decided for Maven is that I am a grumpy old coder ;-).
 *
 *
 */
fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        configureSerialization()
        configureRouting()

    }.start(wait = true)
}
