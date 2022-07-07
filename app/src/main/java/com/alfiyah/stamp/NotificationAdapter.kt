package com.alfiyah.stamp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(private val mContext: Context,
                          private val mNotification: List<Notification>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var stampImage: ImageView
        var userProfileImage: CircleImageView
        var userName: TextView
        var descText: TextView

        init {
            stampImage = itemView.findViewById(R.id.notif_stamp_img)
            userProfileImage = itemView.findViewById(R.id.notif_profile_img)
            userName = itemView.findViewById(R.id.uname_notif)
            descText = itemView.findViewById(R.id.desc_notif)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notif_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mNotification[position]

        if (notification.desctext.equals("started following you")){
            holder.descText.text = "started following you"
        }else if (notification.desctext.equals("liked your stamp")){
            holder.descText.text = "liked your stamp"
        }else if (notification.desctext.contains("commented:")){
            holder.descText.text = notification.desctext.replace("commented:", "commented: ")
        }else{
            holder.descText.text = notification.desctext
        }

        userInfo(holder.userProfileImage, holder.userName, notification.userid)

        if (notification.ispost){
            holder.stampImage.visibility = View.VISIBLE
            getStampImage(holder.stampImage, notification.stampid)
        }
        else{
            holder.stampImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener{
            if (notification.ispost){

                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("stampId", notification.stampid)
                editor.apply()
                (mContext as FragmentActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, StampDetailsFragment()).commit()

            }else{
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("profileId", notification.stampid)
                editor.apply()
                (mContext as FragmentActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    private fun userInfo(imageView: ImageView, userName: TextView, publisherId: String){
        var usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                if(context != null){
//                    return
//                }

                if(snapshot.exists()){
                    val item = snapshot.getValue<Item>(Item::class.java)

                    Picasso.get().load(item!!.getImage()).placeholder(R.drawable.ic_profile).into(imageView)
                    userName.text = item!!.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getStampImage(imageView: ImageView, stampId: String){
        var stampRef = FirebaseDatabase.getInstance()
            .reference.child("Stamps")
            .child(stampId!!)

        stampRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                if(context != null){
//                    return
//                }

                if(snapshot.exists()){
                    val stamp = snapshot.getValue<Stamp>(Stamp::class.java)

                    Picasso.get().load(stamp!!.stampimage).placeholder(R.drawable.ic_profile).into(imageView)


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}