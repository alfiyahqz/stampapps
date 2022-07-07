package com.alfiyah.stamp

data class Stamp (var stampid: String,
         var stampname: String,
         var stampaddress: String,
         var stampreview: String,
         var publisher: String,
         var stampimage: String){

    constructor():this("","","","","","")

}