package de.nosqlgeeks.readys.service

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.repo.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Implement a RESTFul service without sophisticated error handling
 */
fun Route.person() {

    val repo = Repo()

    get("/service/person/{handle}") {

        val handle = call.parameters["handle"]

        try {
            call.respond(repo.getPerson(handle!!))
        } catch (e : Exception) {
            call.respond(HttpStatusCode.NotFound, e.message.toString())
        }
    }

    get("/service/person/search") {
        val query = call.parameters["query"]

        try {
            call.respond(repo.searchPersons(query!!))
        } catch (e : Exception) {
            call.respond(setOf<Person>())
        }
    }
}