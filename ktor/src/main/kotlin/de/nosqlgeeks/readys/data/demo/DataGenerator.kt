package de.nosqlgeeks.readys.data.demo

import de.nosqlgeeks.readys.data.model.Person
import de.nosqlgeeks.readys.data.model.Post
import de.nosqlgeeks.readys.data.model.stats.Click
import de.nosqlgeeks.readys.data.repo.Repo
import java.util.Date

fun generatePersons(repo : Repo) : Person {

    val maggie = Person("Maggie", "Simpson", "maggie@simpsons.com", "maggie", Date(0))
    repo.addPerson(maggie)

    setOf(
        Person("Marge", "Simpson", "marge@simpsons.com", "marge", Date(0)),
        Person("Lisa", "Simpson", "lisa@simpsons.com", "lisa", Date(0)),
        Person("Bart", "Simpson", "bart@simpsons.com", "bart", Date(0)),
        Person("Homer", "Simpson", "homer@simpsons.com", "homer", Date(0)),
        Person("Moe", "Szyslak", "moe@simpsons.com", "moe", Date(0)),
        Person("Todd", "Flanders", "todd@simpsons.com", "todd", Date(0))
    ).forEach {
        it.friends.add(maggie)
        repo.addPerson(it)
    }

    return maggie
}

fun generatePosts(repo : Repo, poster : Person) : Set<Post> {

    val currTime = Date()

    val posts = setOf(
        Post(poster,currTime,"Marjorie Jacqueline \"Marge\" Simpson (née Bouvier[6]; born October 2nd) is the homemaker and matriarch of the Simpson family. She is also one of the five main characters in The Simpsons TV series. Marge is 36 years of age. She and her husband Homer have three children: Bart, Lisa, and Maggie."),
        Post(poster,Date(currTime.time + 100),"Lisa Marie Simpson (born May 9)[9] is the elder daughter and middle child of the Simpson family and one of the two tritagonists (along with Marge,) of The Simpsons series."),
        Post(poster,Date(currTime.time + 200),"Bart is the mischievous, rebellious, misunderstood, disruptive and \"potentially dangerous\" oldest child. He is the only son of Homer and Marge Simpson, and the older brother of Lisa and Maggie. He also has been nicknamed \"Cosmo\", after discovering a comet in \"Bart's Comet\"."),
        Post(poster,Date(currTime.time + 300),"Homer Jay Simpson (born May 12 1956)[29] is the main protagonist and one of the five main characters of The Simpsons series (or show). He is the spouse of Marge Simpson and father of Bart, Lisa and Maggie Simpson. Homer is overweight (said to be ~240 pounds), lazy, and often ignorant to the world around him.")
    )

    posts.forEach{
        repo.addPost(it)
    }

    return posts
}

fun generateClicks(repo: Repo, posts : Set<Post>) {

    val currTime = Date()

    var i = 0

    posts.forEach{
        repo.addClick(Click(currTime.time + i, "moe", it.id))
        i++;
    }
}

fun main(args: Array<String>) {
    val repo = Repo()
    val poster = generatePersons(repo)
    val posts = generatePosts(repo, poster)
    generateClicks(repo, posts)
}