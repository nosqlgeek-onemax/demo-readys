package de.nosqlgeeks.readys.data.repo.redisearch

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import de.nosqlgeeks.readys.data.serialize.GsonFactory
import org.json.JSONArray

/**
 * Helper functions that deal with RediSearch responses
 */
class ResponseHelper {

    private val gson = GsonFactory.f.create()

    fun jsonToJson(input : org.json.JSONArray) : JsonArray {

        return JsonParser.parseString(input.toString()).asJsonArray
    }

    fun jsonToJson(input : org.json.JSONObject) : JsonObject? {

        return JsonParser.parseString(input.toString()).asJsonObject
    }
}