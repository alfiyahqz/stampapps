package com.alfiyah.stamp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ItemAdapter (private var mContext: Context,
                   private var mItem: List<Item>,
                   private var isFragment: Boolean = false) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false)
        return ItemAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        val item = mItem[position]
        holder.userNameTextView.text = item.getUsername()
        Picasso.get().load(item.getImage()).placeholder(R.drawable.ic_profile).into(holder.userProfileImage)

        checkFollowingStatus(item.getUid(), holder.followButton)

        holder.itemView.setOnClickListener(View.OnClickListener {
            if (isFragment){
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", item.getUid())
                pref.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        })

        holder.followButton.setOnClickListener {
            if(holder.followButton.text.toString() == "Follow"){
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(item.getUid())
                            .setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful){
                                    firebaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                                .child("Follow").child(item.getUid())
                                                .child("Followers").child(it1.toString())
                                                .setValue(true).addOnCompleteListener { task ->
                                                    if (task.isSuccessful){

                                                    }
                                                }
                                    }
                                }
                            }
                }
            }else{
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(item.getUid())
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful){
                                    firebaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                                .child("Follow").child(item.getUid())
                                                .child("Followers").child(it1.toString())
                                                .removeValue().addOnCompleteListener { task ->
                                                    if (task.isSuccessful){

                                                    }
                                                }
                                    }
                                }
                            }
                }
            }//end else

            addNotification(item.getUid())

        }//end follow button holder

    }



    override fun getItemCount(): Int {
        return mItem.size
    }

    class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var userNameTextView: TextView = itemView.findViewById(R.id.username_search_txt)
        var userProfileImage: CircleImageView = itemView.findViewById(R.id.profile_search_img)
        var followButton: Button = itemView.findViewById(R.id.follow_btn)
    }

    private fun checkFollowingStatus(uid: String, followButton: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1.toString())
                    .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()){
                    followButton.text = "Following"
                }
                else{
                    followButton.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addNotification(userId: String){
        val notifRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(userId)

        val notifMap = HashMap<String, Any>()
        notifMap["userid"] = firebaseUser!!.uid
        notifMap["desctext"] = "started following you"
        notifMap["stampid"] = ""
        notifMap["ispost"] = false

        notifRef.push().setValue(notifMap)
    }

}
