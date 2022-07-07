package com.alfiyah.stamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {

    private var stampId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentsAdapter: CommentsAdapter? = null
    private var commentList: MutableList<com.alfiyah.stamp.Comment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        stampId = intent.getStringExtra("stampId")
        publisherId = intent.getStringExtra("publisherId")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recycler_view_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentsAdapter = CommentsAdapter(this, commentList as ArrayList<Comment>)
        recyclerView.adapter = commentsAdapter

        userInfo()
        readComment()
        getStampImage()
//        addComments()


        post_comment.setOnClickListener(View.OnClickListener {
            if (add_comments!!.text.toString() == ""){
                Toast.makeText(this@CommentsActivity, "Please write comment first.", Toast.LENGTH_LONG).show()
            }
            else{
                addComments()
            }
        })
    }

    private fun addComments() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(stampId!!)

        val commentMap = HashMap<String, Any>()
        commentMap["comments"] = add_comments!!.text.toString()
        commentMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentMap)
        addNotification()
        add_comments!!.text.clear()

    }

    private fun readComment(){
        val commentRef = FirebaseDatabase.getInstance()
            .reference.child("Comments")
            .child(stampId)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    commentList!!.clear()

                    for (p in snapshot.children){
                        val comment = p.getValue(Comment::class.java)
                        commentList!!.add(comment!!)
                    }

                    commentsAdapter!!.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    private fun addNotification(){
        val notifRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(publisherId!!)

        val notifMap = HashMap<String, Any>()
        notifMap["userid"] = firebaseUser!!.uid
        notifMap["desctext"] = "commented: " + add_comments!!.text.toString()
        notifMap["stampid"] = stampId
        notifMap["ispost"] = true

        notifRef.push().setValue(notifMap)
    }

    private fun userInfo(){
        var usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                if(context != null){
//                    return
//                }

                if(snapshot.exists()){
                    val item = snapshot.getValue<Item>(Item::class.java)

                    Picasso.get().load(item!!.getImage()).placeholder(R.drawable.ic_profile).into(profile_image_comments)


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getStampImage(){
        var stampRef = FirebaseDatabase.getInstance()
            .reference.child("Stamps").child(stampId!!).child("stampimage")

        stampRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                if(context != null){
//                    return
//                }

                if(snapshot.exists()){
                    val image = snapshot.value.toString()

                    Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(stamp_image_comment)


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}