package com.alfiyah.stamp

data class Notification(
    var userid: String = "",
    var desctext: String = "",
    var stampid: String = "",
    var ispost: Boolean = false ) {

    constructor():this("","","", false)
}