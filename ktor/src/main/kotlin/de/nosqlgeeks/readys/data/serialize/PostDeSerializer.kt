package de.nosqlgeeks.readys.data.serialize

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.model.Post
import java.lang.reflect.Type
import java.util.*

class PostDeSerializer : JsonDeserializer<Post> {

    /**
     * Deserializes the person as much as as possible the associated person needs to be completed
     * via the Repo
     */
    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Post {

        if (p0 is JsonObject) {

            val personPlaceHolder = Person.NOBODY
            personPlaceHolder.handle = p0.get("by").asString

            val post = Post(
                personPlaceHolder,
                Date(p0.get("time").asLong),
                p0.get("text").asString
            )

            personPlaceHolder.posts.add(post)

            return post

        } else {
            throw java.lang.Exception("The value %s is not expected as a return type.".format(p0.toString()))
        }

    }
}