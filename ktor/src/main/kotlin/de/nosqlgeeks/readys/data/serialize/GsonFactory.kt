package de.nosqlgeeks.readys.data.serialize
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import de.nosqlgeeks.readys.data.model.Person

/**
 * Builds an instance of GSON that has all the serializers registered
 *
 * ## III.) Let's learn some Kotlin!
 *
 * 1. Kotlin has a bunch of flavours. Which flavour is the best one, depends on what you want to do with the build target.
 * 2. I am using the '-jvm' Kotlin dependencies (see `pom.xml`).
 * 3. If your build target is the JVM, then you can leverage any Java library in your project. This class here uses the
 *    Google's Gson library as the JSON (de-)serializer
 * 4. Also interesting in this class is the usage of the init function (block). This is used instead of a parameter-less
 *    constructor
 * 5. Another interesting aspect is the usage of 'Any'. In Kotlin the 'Any' type represents the super type of
 *    all non-nullable types. It differs to Java's Object in two main things: In Java, primitives types aren't type of
 *    the hierarchy, and you need to box them implicitly, while in Kotlin 'Any' is a super type of all types.
 */
class GsonFactory {

    //The inner Gson builder
    val builder = GsonBuilder();


    init {
        builder.registerTypeAdapter(Person::class.java, PersonNoNestingSerializer())
        builder.registerTypeAdapter(Person::class.java, PersonEmptyRefDeSerializer())
    }

    /**
     * Create and return Gson instance
     */
    fun create() : Gson {
        return builder.create()
    }

    /**
     * Returns a JSON string
     */
    /*
    fun toJSONStr(input : Any) : String {

        return create().toJson(input)
    }
    */

    /**
     * Returns a JSON Element
     */
    /*
    fun toJSON(input : Any) : JsonElement {
        return create().toJsonTree(input)
    }
     */

    /**
     * Implements a singleton of the factory
     */
    companion object {
        val f = GsonFactory()
        val g = f.create()
    }
}