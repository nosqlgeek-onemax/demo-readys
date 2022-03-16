package de.nosqlgeeks.readys.service

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.repo.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
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

    get("/service/person/{handle}/friends") {

        val handle = call.parameters["handle"]

        try {
            call.respond(repo.getPerson(handle!!).friends)
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

    post("/service/person") {
        try {
            val person = call.receive<Person>()
            repo.addPerson(person)
            call.respond(HttpStatusCode.Created, person)
        } catch (e : Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message.toString())
        }
    }

    post("/service/person/{handle}/friends") {

        try {
            val friend = call.receive<Person>()
            val handle = call.parameters["handle"]

            //Update the reference
            val parent = repo.getPerson(handle!!)
            parent.friends.add(friend)
            repo.updatePerson(parent)

            //Add the friend if it does not exist
            try {
                repo.getPerson(friend.handle)
            } catch (e : Exception) {
                if (e.message!!.contains("No such person"))
                    repo.addPerson(friend)
            }

            call.respond(HttpStatusCode.Created, parent.friends)

        } catch (e : Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message.toString())
        }
    }

}