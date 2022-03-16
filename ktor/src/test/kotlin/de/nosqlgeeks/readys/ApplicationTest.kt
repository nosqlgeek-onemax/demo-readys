package de.nosqlgeeks.readys

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.model.Post
import de.nosqlgeeks.readys.data.repo.DBConfig
import de.nosqlgeeks.readys.data.repo.Repo
import de.nosqlgeeks.readys.data.serialize.*
import io.ktor.server.routing.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import de.nosqlgeeks.readys.plugins.*
import de.nosqlgeeks.readys.service.person
import de.nosqlgeeks.readys.service.post
import io.ktor.client.request.*
import io.ktor.client.statement.*
import redis.clients.jedis.Jedis
import java.util.*

class ApplicationTest {

    val cfg = DBConfig()
    var con = Jedis(cfg.host, cfg.port)
    var repo = Repo()

    @BeforeTest
    fun before() {

        //Init the DB connection
        con = Jedis(cfg.host, cfg.port)
        con.auth(cfg.password)
        val r = con.flushAll()
        println("flushed = %s".format(r))

        //Create a repo
        repo = Repo()
        println("Created a repository.")

    }

    @Test
    fun testAddPerson() = testApplication {

        //Configure the test application to use the person routes
        application {
            configureSerialization()
            routing {
                person()
            }
        }

        //Perform a POST request
        val payload = Person("David", "Maier", "david@nosqlgeeks.de", "nosqlgeek", Date(0))
        val jsonPayload = GsonFactory.g.toJson(payload)

        val response = client.post("/service/person") {
            contentType(ContentType.Application.Json)
            setBody(jsonPayload)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        println(response.status)
        println(response.bodyAsText())
    }

    @Test
    fun testAddFriends() = testApplication {
        //Configure the test application to use the person routes
        application {
            configureSerialization()
            routing {
                person()
            }
        }

        //Perform a POST request
        val bart = Person("Bart", "Simpson", "bart@simpsons.com", "bart", Date(0))
        val lisa = Person("Lisa", "Simpson", "lisa@simpsons.com", "lisa", Date(0))

        val resp1 = client.post("/service/person") {
            contentType(ContentType.Application.Json)
            setBody(GsonFactory.g.toJson(bart))
        }

        println("Person created = %s".format(resp1.status.toString()))
        assertEquals(HttpStatusCode.Created, resp1.status)
        println(resp1.bodyAsText())

        val resp2 = client.post("/service/person/bart/friends") {
            contentType(ContentType.Application.Json)
            setBody(GsonFactory.g.toJson(lisa))
        }

        println("Friend added = %s".format(resp2.status.toString()))
        assertEquals(HttpStatusCode.Created, resp2.status)
        println(resp2.bodyAsText())

        val resp3 = client.get("/service/person/bart")
        println("Fetched person again = %s".format(resp3.status.toString()))
        assertEquals(HttpStatusCode.OK, resp3.status)
        println(resp3.bodyAsText())

        val resp4 = client.get("/service/person/bart/friends")
        println("Fetched friends again = %s".format(resp4.status.toString()))
        assertEquals(HttpStatusCode.OK, resp4.status)
        println(resp4.bodyAsText())
    }

    @Test
    fun testAddPost() = testApplication {
        application {
            configureSerialization()
            routing {
                post()
            }
        }

        //Ensure that we have two persons
        val bart = Person("Bart", "Simpson", "bart@simpsons.com", "bart", Date(0))
        val lisa = Person("Lisa", "Simpson", "lisa@simpsons.com", "lisa", Date(0))
        repo.addPerson(bart)
        repo.addPerson(lisa)

        //Post something
        val post = Post(lisa, Date(), "Bart is not always friendly.")

        val resp = client.post("/service/post") {
            contentType(ContentType.Application.Json)
            setBody(GsonFactory.g.toJson(post))
        }

        println("Created a post = %s".format(resp.status.toString()))
        assertEquals(HttpStatusCode.Created, resp.status)
        println(resp.bodyAsText())

        val lisaAgain = repo.getPerson(lisa.handle)
        println("Checking if the back-reference is there ...")
        println(lisaAgain.posts.take(1).get(0).toString())
    }
}