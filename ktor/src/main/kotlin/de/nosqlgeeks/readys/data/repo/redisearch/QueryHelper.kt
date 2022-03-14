package de.nosqlgeeks.readys.data.repo.redisearch

import redis.clients.jedis.search.Query

/**
 * AggregateBuilder takes a String query, but ftQuery takes an object of the type Query as a parameter.
 * It's not possible to get the string query of a query object. This class provides a simple workaround.
 */
class QueryHelper {

    class QueryWrapper(val queryStr : String, val query : Query)

    fun wrapQuery(queryStr: String) : QueryWrapper {
        return QueryWrapper(queryStr, Query(queryStr))
    }

    fun escapeFieldValue(value : String) : String {
        return value.replace(":", "\\:")
    }
}

