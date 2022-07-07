package com.alfiyah.stamp

data class Comment (
    var comments: String,
    var publisher: String ){

    constructor():this("","")
}