package de.nosqlgeeks.readys.data.repo.redisearch

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import de.nosqlgeeks.readys.data.serialize.GsonFactory
import redis.clients.jedis.search.Document
import redis.clients.jedis.search.SearchResult

/**
 * Helper functions that deal with RediSearch responses
 */
class ResponseHelper {

    private val gson = GsonFactory.f.create()


    /**
     * Helper that converts a JSONArray to a GSON JsonArray
     */
    fun jsonToJson(input : org.json.JSONArray) : JsonArray {

        return JsonParser.parseString(input.toString()).asJsonArray
    }

    /**
     * Helper that converts a JSONObject to a GSON JsonObject
     */
    fun jsonToJson(input : org.json.JSONObject) : JsonObject? {

        return JsonParser.parseString(input.toString()).asJsonObject
    }

    /**
     * Helper that converts a RediSearch Document to a JsonObject
     */
    fun docToJson(d: Document) : JsonObject {

        var result = JsonObject()

        result.add("id", gson.toJsonTree(d.id))
        result.add("score", gson.toJsonTree(d.score))

        //There is only one property ($) returned if RediSearch is used on JSON. The value is the string representation of a JSON object


        result.add("value", JsonParser.parseString(d.properties.find { it.key == "$" }?.value.toString()).asJsonObject)

        return result
    }

    /**
     * Helper that converts a RediSearch SearchResult into a JSON object
     */
    fun searchResultToJson(s : SearchResult) : JsonObject {

        var result = JsonObject()

        result.add("total", gson.toJsonTree(s.totalResults))

        val docs = JsonArray(s.documents.size)

        s.documents.forEach{
            docs.add(docToJson(it))
        }

        result.add("docs", docs)

        return result
    }
}