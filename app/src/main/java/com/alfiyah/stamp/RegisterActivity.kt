package com.alfiyah.stamp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        signup_btn.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser() {
        val username: String = username_txt.text.toString()
        val email: String = email_txt.text.toString()
        val password: String = password_txt.text.toString()

        if (username == "") {
            Toast.makeText(this@RegisterActivity, "Please write Username", Toast.LENGTH_LONG).show()
        } else if (email == "") {
            Toast.makeText(this@RegisterActivity, "Please write E-mail", Toast.LENGTH_LONG).show()
        } else if (password == "") {
            Toast.makeText(this@RegisterActivity, "Please write Password", Toast.LENGTH_LONG).show()
        } else {
            val progressDialog = ProgressDialog(this@RegisterActivity)
            progressDialog.setTitle("Sign Up")
            progressDialog.setMessage("Please wait, this may take a while...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        saveUserInfo(username, email, progressDialog)

                    } else {
                        Toast.makeText(this, "Error Message : " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun saveUserInfo(username: String, email: String, progressDialog: ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["username"] = username.toLowerCase()
        userMap["email"] = email
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/stampapps-fcc73.appspot.com/o/Default%20Image%2Fbaby.png?alt=media&token=f975880d-3964-459c-974a-670c8dd04410"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account has been created successfully.", Toast.LENGTH_LONG).show()

                    FirebaseDatabase.getInstance().reference
                            .child("Follow").child(currentUserID)
                            .child("Following").child(currentUserID)
                            .setValue(true)

                    val signIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    signIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(signIntent)
                    finish()
                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }

    }

    override fun onClick(v: View?) {

    }
}