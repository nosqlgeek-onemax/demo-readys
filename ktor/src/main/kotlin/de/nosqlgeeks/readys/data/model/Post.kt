package de.nosqlgeeks.readys.data.model

import java.util.Date
import java.util.zip.CRC32


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
 * 2. Non-nullable types
 *
 * Kotlin tries to avoid null pointer exceptions. So there are nullable and non-nullable types. The default is
 * non-nullable. If you want to be able to assign null, then you need to call it out when declaring your variable.
 * The 'id' property in that example is nullable because the Base64 helper that I am using returns a nullable type.
 */


data class Post(var by : Person,
                var time: Date,
                var text : String
) {

    init {
        by.posts.add(this)
    }

    /**
     * Companion
     */
    companion object {
        fun genId(personHandle : String, time : Long) : String {
            return "%s:%s".format(personHandle, time)
        }
    }

    /**
     * Getters
     */
    val id : String
        get() {
            return genId(by.handle, time.time)
        }

    /**
     * 'Not fun fact' because it took me an hour of debugging:
     *
     * Kotlin data classes are usually great, but they come with some by default implemented methods that
     * don't support circular references (e.g., a post has a person associated, but the post is referenced by the person)
     * because this would end in an endless loop.
     *
     * I am fixing this here by overriding the 'hashCode', 'equals', and 'toString' functions.
     */
    override fun hashCode(): Int {
        val crc = CRC32()
        crc.update(this.id.toByteArray(Charsets.UTF_8))
        return (crc.value % Int.MAX_VALUE).toInt()
    }

    /**
     * Same for equals
     */
    override fun equals(other: Any?): Boolean {

        if (other is Post)
            if (other.id == this.id) return true

        return false
    }

    /**
     * Same for Strings
     */
    override fun toString(): String {
        return "%s:%s".format(this.id, this.text)
    }
}
