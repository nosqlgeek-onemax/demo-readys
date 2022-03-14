package de.nosqlgeeks.readys.data.model.stats

import java.util.Date

/**
 * A class that wraps the access to several statistics, for now just clicks
 */
data class Stats(var from : Date,
            var to: Date,
            var clicks : MutableList<Click> = mutableListOf(),
            var countClicks : Long = 0,
            var countUniqueClicks : Long = 0
) {
    companion object {
        val EMPTY = Stats(Date(0), Date(0))
    }
}
