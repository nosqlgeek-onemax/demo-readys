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
import redis.clients.jedis.search.Schema.TextField
import redis.clients.jedis.search.FieldName
import redis.clients.jedis.search.Schema.Field
import redis.clients.jedis.search.Schema.TagField
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
 *    3. Look at the key generator. It's possible to pass null as a default value, which means that the variable is
 *       declared in the function signature, but it is not mandatory to pass it. This would end in multiple methods with
 *       the same name, but slightly different argument lists in Java.
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

    /**
     * Let's initialize the repo by creating some indexes
     */
    init {
        //Establish a database connection
        cfg = DBConfig()
        redis = JedisPooled(cfg.host, cfg.port, cfg.user, cfg.password)
        logger.log(INFO, "Person index created = %b".format(createPersonIndex()))
        logger.log(INFO, "Post index created = %b".format(createPostIndex()))
    }


    /**
     * Everything in Redis has a key. So let's define some key generators.
     */
    private fun key(prefix : String, id: String?) : String {

            //Ignore the id and just return the prefix. This is different from passing an empty String
            if (id == null)
                return prefix
            else
                return "%s:%s".format(prefix, id)
    }

    private fun personKey(handle: String? = null) : String {
        return key("person", handle)
    }

    private fun postKey(id: String? = null) : String {
        return key("post", id)
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
            .addField(TextField(FieldName("$.firstname", "firstname")))
            .addField(TextField(FieldName("$.lastname", "lastname")))
            .addField(TagField(FieldName("$.handle", "handle"),",",false))
            .addField(TagField(FieldName("$.email", "email"),",",false))
            .addField(Field(FieldName("$.bday","bday"),Schema.FieldType.NUMERIC,false, false))

        return createIdx("idx:%s".format(personKey()), IndexDefinition(JSON).setPrefixes(personKey("")), schema)
    }

    /**
     * Index posts
     */
    private fun createPostIndex() : Boolean {
        val schema = Schema()
            .addTagField("$.by AS by")
            .addNumericField("$.time AS time")
            .addTextField("$.text AS text", 1.0)


        return createIdx("idx:%s".format(postKey()), IndexDefinition(JSON).setPrefixes(postKey("")), schema)

    }


    /**
     * Adds a person without checking if referenced persons are in the database
     */
    override fun addPerson(person: Person): Person {

        //Hint: Using Path2.ROOT_PATH skips the built-in serialization and gives us more control
        redis.jsonSet(personKey(person.handle), Path2.ROOT_PATH, gson.toJson(person))
        return person
    }

    /**
     * Gets a person and all the associated ones
     */
    override fun getPerson(handle: String): Person {

        return getPerson(handle, mutableSetOf())
    }

    /**
     * Recursive function that traverses the tree of friends
     */
    fun getPerson(handle: String, processedPersons : MutableSet<Person>) : Person {

        val result = redis.jsonGet(personKey(handle), Path2.ROOT_PATH)

        //Jedis returns an instance of org.json.JSONArray, but I want to have Gson array
        val personJson = responseHelper.jsonToJson(result as JSONArray)[0]
        var person = g.fromJson(personJson, Person::class.java)

        //Remember persons that were already processed, this allows us to terminate the recursion
        processedPersons.add(person)

        //Add all the friends, and the friends of the friends and so on
        personJson.asJsonObject.get("friends").asJsonArray.forEach{

            //The handle of the friend
            val friendHandle = it.asString
            var friend = processedPersons.find { it.handle == friendHandle  }

            //Recursion
            if (friend == null) {
                friend = getPerson(friendHandle,processedPersons)
                processedPersons.add(friend)
            }

            person.friends.add(friend)
        }

        return person

    }

    /**
     * Simply deletes a person
     */
    override fun delPerson(handle: String): Boolean {

        return (redis.jsonDel(personKey(handle)) == 1L)
    }

    /**
     * The search query needs to follow the RediSearch query syntax, e.g. '@$.firstname:Kurt'.
     *
     * If no field is specified, then RediSearch will search across the text fields. The 'firstname' field is indexed
     * as text, which means that the queries '@$.firstname:Kurt' and 'Kurt' return the same result.
     */
    override fun searchPersons(query: String): Set<Person> {

        //Simple query without sorting
        val q = Query(query)
        val docs = redis.ftSearch("idx:%s".format(personKey()), q)
        val result = responseHelper.searchResultToJson(docs)

        val persons = mutableSetOf<Person>()

        result.getAsJsonArray("docs").forEach{
            val json = it.asJsonObject.get("value").asJsonObject
            val handle = json.get("handle").asString

            //Get person does resolve the references to friends
            persons.add(getPerson(handle))
        }

        return persons
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