package de.nosqlgeeks.readys.data.repo

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.model.Post
import de.nosqlgeeks.readys.data.model.stats.Click
import de.nosqlgeeks.readys.data.serialize.GsonFactory
import kotlin.test.*
import redis.clients.jedis.Jedis
import java.util.*

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
    fun testGetPerson() {
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

    @Test
    fun testGetPersonRecursive() {

        println("-- testGetPersonRecursive")

        val david = Person("David", "Maier", "david@nosqlgeeks.de", "nosqlgeek", Date(0))
        val elena = Person("Elena", "Kolevska", "elena.kolevska@redis.com", "elena_kolevska", Date(0))
        val kurt = Person("Kurt", "Moeller", "kurt.moellera@redis.com", "kurtfm", Date(0))

        //Create a circular dependency
        david.friends.add(elena)
        elena.friends.add(kurt)
        kurt.friends.add(david)

        //Add persons
        val repo = Repo()
        repo.addPerson(david)
        repo.addPerson(elena)
        repo.addPerson(kurt)

        //Get a person - This should not cause a Stack Overflow
        val nosqlgeek = repo.getPerson("nosqlgeek")
        val kurtfm = nosqlgeek.friends.find { it.lastname == "Kolevska" }?.friends?.find { it.firstname == "Kurt" }
        println("The friend of Elena is: %s".format(kurtfm?.firstname))
        assertEquals("Kurt", kurtfm?.firstname)

        val again = kurtfm?.friends?.take(1)?.get(0)
        println("The friend of Kurt is: %s".format(again?.firstname))
        assertEquals("David", again?.firstname)
    }

    @Test
    fun testDelPerson() {

        println("-- testDelPerson")

        val repo = Repo()
        val david = Person("David", "Maier", "david@nosqlgeeks.de", "nosqlgeek", Date(0))
        repo.addPerson(david)

        val deleted = repo.delPerson(david.handle)
        println("deleted = %b".format(deleted))
        assertEquals(true, deleted)
    }


    @Test
    fun testSearchPerson() {

        println("-- testSearchPerson")

        val repo = Repo()
        val kurt = Person("Kurt", "Moeller", "kurt.moellera@redis.com", "kurtfm", Date(0))
        repo.addPerson(kurt)

        val result = repo.searchPersons("Kurt").take(1).get(0)

        assertEquals(kurt.handle, result.handle)
        println("result = %s".format(result.toString()))
    }

    @Test
    fun testSearchPost() {

        println("-- testSearchPost")

        //Prepare the objects
        val david = Person("David", "Maier", "david@nosqlgeeks.de", "nosqlgeek", Date(0))

        val firstPost = Post(david,Date(),"Kotlin is great, and it gets even better with Redis.")
        Thread.sleep(100)
        val secondPost = Post(david,Date(),"Redis with RediSearch and RedisJson provide a ton of development flexibility.")

        assertEquals(2, david.posts.size)

        //Store objects
        val repo = Repo()
        repo.addPerson(david)
        repo.addPost(firstPost)
        repo.addPost(secondPost)

        //Search the posts by checking if the result also fetched the persons
        val posts = repo.searchPosts("Redis")

        assertEquals("Kotlin is great, and it gets even better with Redis.",posts.elementAt(0).text)
        assertEquals("Redis with RediSearch and RedisJson provide a ton of development flexibility.",posts.elementAt(1).text)
        assertEquals("david@nosqlgeeks.de", posts.elementAt(0).by.email)

        println(posts)
    }

    /**
     * We are going to repurpose this one in two tests
     */
    fun addClick() : Post {

        val nosqlgeek = Person("David", "Maier", "david@nosqlgeeks.de", "nosqlgeek", Date(0))
        val elena = Person("Elena", "Kolevska", "elena.kolevska@redis.com", "elena_kolevska", Date(0))
        val post = Post(nosqlgeek,Date(),"Kotlin is great, and it gets even better with Redis.")

        val repo = Repo()
        repo.addPerson(nosqlgeek)
        repo.addPerson(elena)
        repo.addPost(post)

        Thread.sleep(100)
        val timeClicked = Date().time
        val click = Click(timeClicked,elena.handle, post.id)
        repo.addClick(click)
        val clickKey = "click:elena_kolevska:%d".format(timeClicked)
        assertTrue(con.exists(clickKey))
        println(con.hgetAll(clickKey))

        return post
    }

    @Test
    fun testAddClickt() {
        println("-- testAddClick")
        addClick()
    }

    @Test
    fun testGetStatsByPost() {
        println("-- testGetStatsByPost")
        val post = addClick()
        Thread.sleep(100)
        val click = Click(Date().time,"user", post.id)
        Thread.sleep(100)
        val click2 = Click(Date().time,"user", post.id)

        val repo = Repo()
        repo.addClick(click)
        repo.addClick(click2)

        val stats = repo.getStatsByPost(post.id)
        assertEquals(3, stats.countClicks)
        assertEquals(2, stats.countUniqueClicks)

        println(stats)
    }
}