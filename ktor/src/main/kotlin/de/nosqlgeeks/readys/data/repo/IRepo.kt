package de.nosqlgeeks.readys.data.repo

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.model.Post
import de.nosqlgeeks.readys.data.model.stats.Click
import de.nosqlgeeks.readys.data.model.stats.Stats

/**
 * Describes a repository that is used to perform CRUD operations to the underlying database
 *
 * ## IV.) Let's learn some Kotlin!
 *
 * 1. Interfaces
 *
 * Interfaces work similar to Java. A class can implement such an interface by overriding its functions.
 *
 * BTW: There is also inheritance of classes possible. A class needs to be declared as 'open' in order to be able
 * to inherit from it.
 */
interface IRepo {

    //Persons
    fun addPerson(person : Person) : Person
    fun updatePerson(person : Person) : Person
    fun getPerson(handle : String) : Person
    fun delPerson(handle : String) : Boolean
    fun searchPersons(query : String) : Set<Person>

    //Posts
    fun addPost(post : Post) : Post
    fun getPost(id : String) : Post
    fun delPost(id : String) : Boolean
    fun searchPosts(query : String) : Set<Post>

    //Stats
    fun addClick(click : Click) : Click
    fun getStatsByPost(id : String) : Stats
    fun getStatsByPerson(handle : String) : Stats
    fun getAllStats() : Stats
}