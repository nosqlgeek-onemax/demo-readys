package de.nosqlgeeks.readys.data.model.stats

import java.util.Date

data class Stats(var from : Date,
                 var to: Date,
                 var clicks : MutableList<Click> = mutableListOf()
)
