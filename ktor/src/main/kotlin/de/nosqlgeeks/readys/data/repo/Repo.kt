package de.nosqlgeeks.readys.data.repo

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.model.Post
import de.nosqlgeeks.readys.data.model.stats.Click
import de.nosqlgeeks.readys.data.model.stats.Stats
import de.nosqlgeeks.readys.data.repo.redisearch.ResponseHelper
import de.nosqlgeeks.readys.data.serialize.GsonFactory
import de.nosqlgeeks.readys.data.serialize.GsonFactory.Companion.g
import org.json.JSONArray
import redis.clients.jedis.*
import redis.clients.jedis.exceptions.JedisDataException
import redis.clients.jedis.json.Path2
import redis.clients.jedis.search.*
import redis.clients.jedis.search.IndexDefinition.Type.*
import java.util.logging.Logger
import java.util.logging.Level.*

/**
 * Implements the repository based on RediSearch
 *
 * ## VI.) Let's learn some Kotlin!
 *
 *    1. Kotlin supports explicit type casting by using the 'as' operator.
 *    2. You can also use implicit type casting by checking in an if condition via the 'is' operator, then
 *       the checked variable will be automatically casted within the if block.
 *
 * ## I.) Let's learn about RedisJSON!
 *
 * 1. We are going to index on JSON documents by using RediSearch. RediSearch uses the notion of an index and
 *    a schema. The schema contains all fields and indexes that we want to cover. There are several index
 *    structures used behind the scenes. Text fields are indexed with the help of an inverted index (good for
 *    full-text search). Tag fields us a simpler indexing mechanisms. This is index structure is better for
 *    queries that match one or multiple exact values.
 * 2. We can index on JSON and also on Hashes (similar to dictionaries in Redis). Our index here only considers
 *    Hashes that have a key with the prefix 'person:'
 *
 */
class Repo : IRepo {

    //Global variables
    private val logger = Logger.getLogger("repo")
    private val cfg : DBConfig
    private val gson = GsonFactory.g
    private  val responseHelper = ResponseHelper()

    var redis : UnifiedJedis

    init {
        //Establish a database connection
        cfg = DBConfig()
        redis = JedisPooled(cfg.host, cfg.port, cfg.user, cfg.password)
        logger.log(INFO, "index created = %b".format(createPersonIndex()))
        logger.log(INFO, "index created = %b".format(createPostIndex()))
    }


    /**
     * Tries to create an index
     *
     * Returns true if the index could be created and false if the index already existed.
     */
    private fun createIdx(name : String, def : IndexDefinition, schema : Schema) : Boolean {

        try {

            val opts = IndexOptions.defaultOptions().setDefinition(def)
            redis.ftCreate(name, opts, schema)
            return true

        } catch (e : JedisDataException) {

            if (e.message == "Index already exists") return false
            else throw e
        }
    }


    /**
     * Index persons
     */
    private fun createPersonIndex() : Boolean {

        val schema = Schema()
            .addTextField("$.firstname AS firstname", 1.0)
            .addTextField("$.lastname AS lastname", 1.0)
            .addTagField("$.handle AS handle")
            .addTagField("$.email AS email")
            .addNumericField("$.bday AS bday")

        return createIdx("idx:person", IndexDefinition(JSON).setPrefixes("person:"), schema)
    }

    /**
     * Index posts
     */
    private fun createPostIndex() : Boolean {
        val schema = Schema()
            .addTagField("$.by AS by")
            .addNumericField("$.time AS time")
            .addTextField("$.text AS text", 1.0)


        return createIdx("idx:person", IndexDefinition(JSON).setPrefixes("post:"), schema)

    }

    override fun addPerson(person: Person): Person {

        //Hint: Using Path2.ROOT_PATH skips the built-in serialization and gives us more control
        redis.jsonSet("person:".plus(person.handle), Path2.ROOT_PATH, gson.toJson(person))
        return person
    }

    override fun getPerson(handle: String): Person {

        return getPerson(handle, Person.NOBODY)

    }


    fun getPerson(handle: String, parent : Person) : Person {

        val result = redis.jsonGet("person:".plus(handle), Path2.ROOT_PATH)

        //Jedis returns an instance of org.json.JSONArray, but I want to have Gson array
        val personJson = responseHelper.jsonToJson(result as JSONArray)[0]
        var person = g.fromJson(personJson, Person::class.java)

        //Recursive call
        personJson.asJsonObject.get("friends").asJsonArray.forEach{
            val friendHandle = it.asString

            if (friendHandle == parent.handle) {
                person.friends.add(parent)
            } else {
                val friend = getPerson(friendHandle, person)
                person.friends.add(friend)
            }
        }

        return person

    }

    override fun delPerson(handle: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun searchPersons(query: String): Set<Person> {
        TODO("Not yet implemented")
    }

    override fun addPost(post: Post): Post {
        TODO("Not yet implemented")
    }

    override fun getPost(id: String): Post {
        TODO("Not yet implemented")
    }

    override fun delPost(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun searchPosts(query: String) {
        TODO("Not yet implemented")
    }

    override fun addClick(click: Click): Click {
        TODO("Not yet implemented")
    }

    override fun getStatsByPost(id: String): Stats {
        TODO("Not yet implemented")
    }

    override fun getStatsByPerson(handle: String): Stats {
        TODO("Not yet implemented")
    }

    override fun getAllStats(): Stats {
        TODO("Not yet implemented")
    }

    override fun flushStats(): Boolean {
        TODO("Not yet implemented")
    }
}