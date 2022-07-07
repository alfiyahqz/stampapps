package com.alfiyah.stamp

class Item {
    private var username: String = ""
    private var uid: String = ""
    private var email: String = ""
    private var password: String = ""
    private var image: String = ""

    constructor()

    constructor(username: String, uid: String, email: String, password: String, image: String){
        this.username = username
        this.uid = uid
        this.email = email
        this.password = password
        this.image = image
    }

    fun getUsername(): String{
        return username
    }

    fun setUsername(username: String){
        this.username = username
    }

    fun getUid(): String{
        return uid
    }

    fun setUid(uid: String){
        this.uid = uid
    }

    fun getEmail(): String{
        return email
    }

    fun setEmail(email: String){
        this.email = email
    }

    fun getPass(): String{
        return password
    }

    fun setPass(password: String){
        this.password = password
    }

    fun getImage(): String{
        return image
    }

    fun setImage(image: String){
        this.image = image
    }
}
