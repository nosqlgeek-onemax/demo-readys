package de.nosqlgeeks.readys.data.model.stats

import java.util.Date

data class Stats(val from : Date,
                 val to: Date,
                 val clicks : MutableList<Click> = mutableListOf()
)
