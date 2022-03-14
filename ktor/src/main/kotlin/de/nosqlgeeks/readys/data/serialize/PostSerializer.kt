package de.nosqlgeeks.readys.data.serialize

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import de.nosqlgeeks.readys.data.model.Post
import java.lang.reflect.Type

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