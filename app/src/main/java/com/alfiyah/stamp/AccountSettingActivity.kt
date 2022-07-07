package com.alfiyah.stamp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri : Uri? = null
    private var storageProfilePicRef: StorageReference? = null
//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Picture")

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val signOutIntent = Intent(this@AccountSettingActivity, JoinActivity::class.java)
            signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(signOutIntent)
            finish()
        }

        change_img_btn.setOnClickListener {
            checker ="clicked"
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingActivity)
        }

        close_btn_add.setOnClickListener {
            onBackPressed()

        }

//        Picasso.get()
//          .load("https://firebasestorage.googleapis.com/v0/b/stampapps-fcc73.appspot.com/o/Default%20Image%2Fbaby.png?alt=media&token=f975880d-3964-459c-974a-670c8dd04410")
//          .fit()
//          .into(profile_img)

        update_btn.setOnClickListener {
            checker = "clicked"
//            uploadImage()
            if (imageUri == null){
                updateData()
            } else{
                uploadImage()
            }
//            updateData()
//            onBackPressed()
        }

        userInfo()
    }

    //change image
    private fun uploadImage() {
        when
        {
            imageUri == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username_txt.text.toString()) -> Toast.makeText(this, "Please write user name first.", Toast.LENGTH_LONG).show()
            email_txt.text.toString() == "" -> Toast.makeText(this, "Please write email first.", Toast.LENGTH_LONG).show()


            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile...")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener<Uri> {task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["username"] = username_txt.text.toString()
                        userMap["email"] = email_txt.text.toString()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Account Information has been updated successfully.", Toast.LENGTH_LONG).show()

                        finish()
                        progressDialog.dismiss()
                    }
                    else
                    {
                        progressDialog.dismiss()
                    }
                } )
            }
        }
    }//end upload image


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_img.setImageURI(imageUri)
        }
    }

    //update button
    private fun updateData(){
        when {
            TextUtils.isEmpty(username_txt.text.toString()) -> Toast.makeText(this, "Please write user name first.", Toast.LENGTH_LONG).show()
            email_txt.text.toString() == "" -> Toast.makeText(this, "Please write email first.", Toast.LENGTH_LONG).show()
//            password_txt.text.toString() == "" -> Toast.makeText(this, "Please write your bio first.", Toast.LENGTH_LONG).show()
            else -> {

                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                //email password havent shown up???
                val userMap = HashMap<String, Any>()
                userMap["username"] = username_txt.text.toString().toLowerCase()
                userMap["email"] = email_txt.text.toString().toLowerCase()

//                userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/stampapps-fcc73.appspot.com/o/Default%20Image%2Fbaby.png?alt=media&token=f975880d-3964-459c-974a-670c8dd04410"

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account Information has been updated successfully.", Toast.LENGTH_LONG).show()

//                val intent = Intent(this@AccountSettingActivity, ProfileFragment::class.java)
//                startActivity(intent)
                finish()

            }
        }
    }


    private fun userInfo(){
        var usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                if(context != null){
//                    return
//                }

                if(snapshot.exists()){
                    val item = snapshot.getValue<Item>(Item::class.java)

                    Picasso.get().load(item!!.getImage()).placeholder(R.drawable.ic_profile).into(profile_img)
                    username_txt.setText(item!!.getUsername())
                    email_txt.setText(item!!.getEmail())


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}