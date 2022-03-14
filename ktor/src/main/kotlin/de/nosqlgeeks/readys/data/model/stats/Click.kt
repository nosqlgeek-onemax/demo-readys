package de.nosqlgeeks.readys.data.model.stats

data class Click(var time : Long,
                 var by : String,
                 var postid : String) {

    val id : String = "%s:%d".format(by, time)
}


