package de.nosqlgeeks.readys.data.serialize

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import de.nosqlgeeks.readys.data.model.Person
import java.lang.reflect.Type

/**
 * Serializes a Person to JSON
 *
 * Using a custom serializer is not mandatory, but it helps us to massage the date better for storage purposes.
 * We are using it here to avoid recursive embeddings of persons (e.g., a person that has a person as a friend that has
 * the same person as a friend). This is solved by only storing the reference to the person in the serialized output.
 */
class PersonSerializer : JsonSerializer<Person> {

    override fun serialize(p0: Person?, p1: Type?, p2: JsonSerializationContext?): JsonElement {

        val j  = JsonObject()

        if (p0 != null) {
            j.addProperty("handle", p0.handle)
            j.addProperty("firstname", p0.firstname)
            j.addProperty("lastname", p0.lastname)
            j.addProperty("email", p0.email)
            j.addProperty("bday", p0.bday.time)

            var friends = JsonArray();
            p0.friends.forEach{ friends.add(it.handle) }
            if (!friends.isEmpty)
                j.add("friends", friends)

            var posts = JsonArray();
            p0.posts.forEach{ posts.add(it.id) }
            if (!posts.isEmpty)
                j.add("posts", posts)
        }

        return j
    }

}