package de.nosqlgeeks.readys.data

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.serialize.GsonFactory
import kotlin.test.*
import java.util.Date


/**
 * ## VI.) Let's learn some Kotlin!
 *
 * 1. Testing
 *
 * You can use several Java test libraries. Test methods are indeed again expressed
 * as functions that are annotated with @Test. This test case doesn't use a JUnit import.
 * Kotlin abstracts this away. JUnit seems to be still used behind the scenes:
 * https://kotlinlang.org/docs/jvm-test-using-junit.html
 *
 * 2. Iterations with lambdas
 *
 * Kotlin is a mixed creature of object-oriented and functional programming
 */
class PersonTest {

    @Test
    fun testGetFriends() {

        val david = Person("David", "Maier", "david@nosqlgeeks.de", "nosqlgeek", Date())
        val elena = Person("Elena", "Kolevska", "elena.kolevska@redis.com", "elena_kolevska", Date())
        david.friends.add(elena)

        assertTrue(david.friends.size == 1)
        assertTrue(elena.friends.size == 0)
        david.friends.forEach { println("%s %s".format(it.firstname, it.lastname)) }


        elena.friends.add(david)
        assertTrue(elena.friends.size == 1)
        elena.friends.forEach { println("%s %s".format(it.firstname, it.lastname)) }
    }

    @Test
    fun testPersonToJSON()  {

        val david = Person("David", "Maier", "david@nosqlgeeks.de", "nosqlgeek", Date(0))
        val elena = Person("Elena", "Kolevska", "elena.kolevska@redis.com", "elena_kolevska", Date(0))
        david.friends.add(elena)

        val j = GsonFactory.g.toJson(david)

        assertEquals("{\"handle\":\"nosqlgeek\",\"firstname\":\"David\",\"lastname\":\"Maier\",\"email\":\"david@nosqlgeeks.de\",\"bday\":0,\"friends\":[\"elena_kolevska\"]}", j)
        println(j)
    }

}