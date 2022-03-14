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
fun Route.post() {

    val repo = Repo()

    get("/service/post/{id}") {

        val id = call.parameters["id"]

        try {
            call.respond(repo.getPost(id!!))
        } catch (e : java.lang.Exception) {
            call.response.status(HttpStatusCode.NotFound)
        }
    }

    get("/service/post/search") {

        val query = call.parameters["query"]

        try {
            call.respond(repo.searchPosts(query!!))
        } catch (e : java.lang.Exception) {
            call.respond(setOf<Person>())
        }
    }
}