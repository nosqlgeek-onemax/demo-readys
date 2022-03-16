package de.nosqlgeeks.readys.service

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.model.Post
import de.nosqlgeeks.readys.data.repo.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
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
        } catch (e : Exception) {
            call.respond(HttpStatusCode.NotFound, e.message.toString())
        }
    }

    get("/service/post/search") {

        val query = call.parameters["query"]

        try {
            call.respond(repo.searchPosts(query!!))
        } catch (e : Exception) {
            call.respond(setOf<Person>())
        }
    }

    post("/service/post") {

        try {
            val post = call.receive<Post>()

            repo.addPost(post)
            post.by = repo.getPerson(post.by.handle)
            post.by.posts.add(post)
            repo.updatePerson(post.by)

            call.respond(HttpStatusCode.Created, post)

        } catch (e : Exception) {

            call.respond(HttpStatusCode.InternalServerError, e.message.toString())
        }

    }
}