package de.nosqlgeeks.readys.data.serialize

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import de.nosqlgeeks.readys.data.model.Post
import java.lang.reflect.Type

/**
 * II.I) Let's learn some Kotlin!
 *
 * 1. Non-nullable types
 * Kotlin tries to avoid null pointer exceptions. So there are nullable and non-nullable types. The default is
 * non-nullable. If you want to be able to assign null, then you need to call it out when declaring your variable.
 */
class PostSerializer : JsonSerializer<Post> {

    override fun serialize(p0: Post?, p1: Type?, p2: JsonSerializationContext?): JsonElement {

        val j = JsonObject()

        if (p0 != null) {
            j.addProperty("by", p0.by.handle )
            j.addProperty("text",p0.text)
            j.addProperty("time", p0.time.time)
        }

        return j
    }
}