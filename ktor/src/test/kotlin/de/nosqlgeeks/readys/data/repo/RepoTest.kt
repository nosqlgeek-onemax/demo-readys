package de.nosqlgeeks.readys.data.repo

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.serialize.GsonFactory
import kotlin.test.*
import redis.clients.jedis.Jedis
import java.util.*


/**
 * # V.) Let's learn some Kotlin
 *
 * 1. This test case doesn't use a JUnit dependency. Kotlin abstracts this away.
 * 2. JUnit seems to be still used behind the scenes: https://kotlinlang.org/docs/jvm-test-using-junit.html
 */
class RepoTest {

    val cfg = DBConfig()
    var con : Jedis = Jedis(cfg.host, cfg.port)

    @BeforeTest
    fun before() {
        con = Jedis(cfg.host, cfg.port)
        con.auth(cfg.password)
        val r = con.flushAll()
        println("flushed = %s".format(r))
    }

    @Test
    fun testDbConnection() {

        println("-- testDbConnection")
        val repo = Repo()
        val size = repo.redis.dbSize()

        assertEquals(0, size)
        println("db size = %d".format(size))
    }

    @Test
    fun testAddPerson() {
        println("-- testAddPerson")
        val david = Person("David", "Maier", "david@nosqlgeeks.de", "nosqlgeek", Date())
        val elena = Person("Elena", "Kolevska", "elena.kolevska@redis.com", "elena_kolevska", Date())
        david.friends.add(elena)

        println("Adding persons ...")
        val repo = Repo()
        repo.addPerson(david)
        repo.addPerson(elena)

        println("Checking if persons exist ...")
        assertTrue(con.exists("person:" + david.handle))
        println("exists = %s".format(david.handle))
        assertTrue(con.exists("person:" + elena.handle))
        println("exists = %s".format(elena.handle))
    }

    @Test
    fun getPerson() {
        println("-- testGetPerson")

        val david = Person("David", "Maier", "david@nosqlgeeks.de", "nosqlgeek", Date(0))
        val elena = Person("Elena", "Kolevska", "elena.kolevska@redis.com", "elena_kolevska", Date(0))
        david.friends.add(elena)
        elena.friends.add(david)

        println("Adding persons ...")
        val repo = Repo()
        repo.addPerson(david)
        repo.addPerson(elena)

        println("Getting persons ...")
        val person = repo.getPerson("nosqlgeek")
        val personJson = GsonFactory.g.toJson(person)

        assertEquals("{\"handle\":\"nosqlgeek\",\"firstname\":\"David\",\"lastname\":\"Maier\",\"email\":\"david@nosqlgeeks.de\",\"bday\":0,\"friends\":[\"elena_kolevska\"]}", personJson)
        println(personJson)
        assertEquals(1, person.friends.size)
        val friend = person.friends.take(1).get(0)
        assertEquals("Elena", friend.firstname)
        val friendJson = GsonFactory.g.toJson(friend)
        println(friendJson)
        assertEquals("{\"handle\":\"elena_kolevska\",\"firstname\":\"Elena\",\"lastname\":\"Kolevska\",\"email\":\"elena.kolevska@redis.com\",\"bday\":0,\"friends\":[\"nosqlgeek\"]}", friendJson)

    }


}