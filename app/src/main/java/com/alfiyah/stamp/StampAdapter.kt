package com.alfiyah.stamp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class StampAdapter (private var mContext: Context,
                    private var mStamp: List<Stamp>) : RecyclerView.Adapter<StampAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null
//    private lateinit var database: DatabaseReference


    inner class ViewHolder(@NonNull compView: View) : RecyclerView.ViewHolder(compView){
        var profileImage: CircleImageView
        var profileUser: TextView
        var stampImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var likes: TextView
        var publisher: TextView
        var stampName: TextView
        var stampAddress: TextView
        var stampReview: TextView
        var comments: TextView

        init {
            profileImage = compView.findViewById(R.id.user_profile_image_stamp)
            profileUser = compView.findViewById(R.id.user_name_stamp)
            stampImage = compView.findViewById(R.id.post_image_stamp)
            likeButton = compView.findViewById(R.id.post_image_like_btn)
            commentButton = compView.findViewById(R.id.post_image_comment_btn)
            saveButton = compView.findViewById(R.id.post_save_comment_btn)
            likes = compView.findViewById(R.id.likes)
            publisher = compView.findViewById(R.id.publisher)
            stampName = compView.findViewById(R.id.stamp_name)
            stampAddress = compView.findViewById(R.id.stamp_address)
            stampReview = compView.findViewById(R.id.stamp_review)
            comments = compView.findViewById(R.id.comments)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.stamp_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

//        database = Firebase.database.getReference("")
        val stamp = mStamp[position]

        Picasso.get().load(stamp.stampimage).into(holder.stampImage)
        Log.e("tag",stamp.stampimage)

        holder.stampName.visibility == View.VISIBLE
        holder.stampName.setText(stamp.stampname)

        holder.stampAddress.visibility == View.VISIBLE
        holder.stampAddress.setText(stamp.stampaddress)

        holder.stampReview.visibility == View.VISIBLE
        holder.stampReview.setText(stamp.stampreview)

        publisherInfo(holder.profileImage, holder.profileUser, holder.publisher, stamp.publisher)

        isLikes(stamp.stampid, holder.likeButton)
        numberOfLikes(holder.likes, stamp.stampid)
        getTotalComments(holder.comments, stamp.stampid)
        checkSavedStatus(stamp.stampid, holder.saveButton)

        holder.commentButton.setOnClickListener{
            val commentIntent = Intent(mContext, CommentsActivity::class.java)
            commentIntent.putExtra("stampId", stamp.stampid)
            commentIntent.putExtra("publisherId", stamp.publisher)
            mContext.startActivity(commentIntent)
        }

        holder.comments.setOnClickListener{
            val commentIntent = Intent(mContext, CommentsActivity::class.java)
            commentIntent.putExtra("stampId", stamp.stampid)
            commentIntent.putExtra("publisherId", stamp.publisher)
            mContext.startActivity(commentIntent)
        }

        holder.likeButton.setOnClickListener{
            if (holder.likeButton.tag == "Like"){
                FirebaseDatabase.getInstance().reference
                        .child("Likes")
                        .child(stamp.stampid)
                        .child(firebaseUser!!.uid)
                        .setValue(true)

                addNotification(stamp.publisher, stamp.stampid)
            }
            else{
                FirebaseDatabase.getInstance().reference
                        .child("Likes")
                        .child(stamp.stampid)
                        .child(firebaseUser!!.uid)
                        .removeValue()
                val likeIntent = Intent(mContext, MenuActivity::class.java)
                mContext.startActivity(likeIntent)

            }
        }

        holder.likes.setOnClickListener {
            val likesIntent = Intent(mContext, ShowUsersActivity::class.java)
            likesIntent.putExtra("id", stamp.stampid)
            likesIntent.putExtra("title", "likes")
            mContext.startActivity(likesIntent)
        }

        holder.saveButton.setOnClickListener{
            if (holder.saveButton.tag == "Save"){
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(stamp.stampid)
                    .setValue(true)
            }
            else{
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(stamp.stampid)
                    .removeValue()

            }

        }


    }

    private fun checkSavedStatus(stampid: String, imageView: ImageView){
        val savesRef = FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)

        savesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(stampid).exists()){
                    imageView.setImageResource(R.drawable.ic_bookmark_fill)
                    imageView.tag = "Saved"
                }
                else{
                    imageView.setImageResource(R.drawable.ic_bookmark_outline)
                    imageView.tag = "Save"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun numberOfLikes(likes: TextView, stampid: String) {
        val likesRef = FirebaseDatabase.getInstance().reference
                .child("Likes").child(stampid)
        likesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    likes.text = snapshot.childrenCount.toString() + " likes"
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun getTotalComments(comments: TextView, stampid: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(stampid)
        commentsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    comments.text = "view all " + snapshot.childrenCount.toString() + " comments"
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun isLikes(stampid: String, likeButton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val likesRef = FirebaseDatabase.getInstance().reference
                .child("Likes").child(stampid)
        likesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()){
                    likeButton.setImageResource(R.drawable.ic_heart)
                    likeButton.tag = "Liked"
                }
                else{
                    likeButton.setImageResource(R.drawable.ic_heart_outline)
                    likeButton.tag = "Like"
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun publisherInfo(profileImage: CircleImageView, profileUser: TextView, publisher: TextView, publisherID: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val item = snapshot.getValue<Item>(Item::class.java)

                    Picasso.get().load(item!!.getImage()).placeholder(R.drawable.ic_profile).into(profileImage)
                    profileUser.setText(item!!.getUsername())
                    publisher.setText(item!!.getUsername() + "'s review :" )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun addNotification(userId: String, stampid: String){
        val notifRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(userId)

        val notifMap = HashMap<String, Any>()
        notifMap["userid"] = firebaseUser!!.uid
        notifMap["desctext"] = "liked your stamp"
        notifMap["stampid"] = stampid
        notifMap["ispost"] = true

        notifRef.push().setValue(notifMap)
    }


    override fun getItemCount(): Int {
        return mStamp.size
    }
}