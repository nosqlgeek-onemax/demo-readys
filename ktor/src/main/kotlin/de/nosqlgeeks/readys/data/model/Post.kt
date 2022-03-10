package de.nosqlgeeks.readys.data.model

import java.util.Base64
import java.util.Date


/**
 * This class describes a post.
 *
 * ## II.) Let's learn some Kotlin!
 *
 * 1. Companion objects
 *
 * Kotlin doesn't like static functions. It allows static objects by limiting it to one companion object per class.
 * Here an example how to use a companion to implement a helper function that might be useful outside of the context
 * of the state of an instance of a Post.
 *
 * 2. Non-nullable objects
 *
 * Kotlin tries to avoid null pointer exceptions. So there are nullable and non-nullable types. The default is
 * non-nullable. If you want to be able to assign null, then you need to call it out when declaring your variable.
 * The 'id' property in that example is nullable because the Base64 helper that I am using returns a nullable type.
 */


data class Post(val by : Person,
                val time: Date,
                val text : String
) {

    /**
     * Companion
     */
    companion object {
        fun genId(personHandle : String, time : Date) : String? {

            val id = (personHandle + "#" + time.time).encodeToByteArray()
            return Base64.getEncoder().encodeToString(id)
        }
    }

    /**
     * Getters
     */
    val id : String?
        get() {
            return genId(by.handle, time)
        }
}
