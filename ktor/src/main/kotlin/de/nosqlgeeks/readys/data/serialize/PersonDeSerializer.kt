package de.nosqlgeeks.readys.data.serialize

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import de.nosqlgeeks.readys.data.model.Person
import java.lang.reflect.Type
import java.util.*

/**
 * Deserializes a person JSON object by skipping the references to persons and posts for now.
 * This allows us to resolve them later.
 */
class PersonDeSerializer : JsonDeserializer<Person> {

    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Person {

        if (p0 is JsonObject) {

            val person = Person(
                p0.get("firstname").asString,
                p0.get("lastname").asString,
                p0.get("email").asString,
                p0.get("handle").asString,
                Date(p0.get("bday").asLong)
            )

            return person

        } else {
            throw java.lang.Exception("The value %s is not expected as a return type.".format(p0.toString()))
        }
    }

}