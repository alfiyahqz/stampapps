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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_stamp.*

class AddStampActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri : Uri? = null
    private var storageStampPicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stamp)

        storageStampPicRef = FirebaseStorage.getInstance().reference.child("Stamps Picture")

        upload_stamp_btn.setOnClickListener{
            uploadStamp()
//            val afterIntent = Intent(this@AddStampActivity, MenuActivity::class.java)
//            afterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(afterIntent)
//            finish()
        }

        close_btn.setOnClickListener {
//            onBackPressed()
            val updateintent = Intent(this@AddStampActivity, MenuActivity::class.java)
            startActivity(updateintent)
        }

        image_stamp.setOnClickListener {
            CropImage.activity()
                    .setAspectRatio(1,1)
                    .setFixAspectRatio(false)
                    .start(this@AddStampActivity)
        }
    }

    private fun uploadStamp() {
        when {
            imageUri == null -> Toast.makeText(
                this,
                "Please select image first.",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(stamp_name_txt.text.toString()) -> Toast.makeText(
                this,
                "Please write the place's name.",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(stamp_address_txt.text.toString()) -> Toast.makeText(
                this,
                "Please write the place's address.",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(stamp_review_txt.text.toString()) -> Toast.makeText(
                this,
                "Please write the place's review.",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding New Stamp")
                progressDialog.setMessage("Please wait, we are adding your stamp...")
                progressDialog.show()

                val fileRef = storageStampPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")
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
                }).addOnCompleteListener (OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Stamps")

                        val stampId = ref.push().key

                        val stampMap = HashMap<String, Any>()
                        stampMap["stampid"] = stampId!!
                        stampMap["stampname"] = stamp_name_txt.text.toString()
                        stampMap["stampaddress"] = stamp_address_txt.text.toString()
                        stampMap["stampreview"] = stamp_review_txt.text.toString()
                        stampMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        stampMap["stampimage"] = myUrl

                        ref.child(stampId).updateChildren(stampMap)

                        Toast.makeText(this, "Stamp uploaded successfully.", Toast.LENGTH_LONG).show()
                        val updateintent = Intent(this@AddStampActivity, MenuActivity::class.java)
                        startActivity(updateintent)
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
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_stamp.setImageURI(imageUri)
        }
    }
}