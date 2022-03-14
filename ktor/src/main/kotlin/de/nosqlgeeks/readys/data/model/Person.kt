package de.nosqlgeeks.readys.data.model

import java.util.Date


/**
 * This class describes a person.
 *
 * ## I.) Let's learn some Kotlin!
 *
 * 1. Data classes:
 *
 * Main purpose of data classes is to hold data. The compiler automatically derives functions like 'copy' or 'toString'
 * from the properties that are passed to the default constructor of a data class. This data class doesn't have any
 * business logic, which means that we don't have a class body.
 *
 * 2. Static typing
 *
 * Variables can declared via var or val. Kotlin is anyway statically typed. As soon as the variable gets assigned, the
 * type is written in stone. Parameters are strictly typed, e.g. 'firstName : String')
 *
 * 3. Mutable types
 *
 * There are two types of sets here. The 'Set' doesn't have a function 'add', whereby the 'MutableSet' allows you to
 * change the set later by adding elements.
 *
 * BTW: It's not untypical to use built-in shortcuts like 'mutableSetOf()' instead of 'MutableSet<Person>()'.
 *
 * 4. Default values
 *
 * It's possible to set default values if a property isn't passed.
 *
 */
data class Person(var firstname: String,
                  var lastname: String,
                  var email : String,
                  var handle : String,
                  var bday: Date,
                  val friends : MutableSet<Person> = mutableSetOf(),
                  val posts : MutableSet<Post> = mutableSetOf()
) {
    companion object {
        val NOBODY = Person("","","","nobody", Date(0), mutableSetOf(), mutableSetOf())
    }
}