package com.alfiyah.stamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var stampList: List<Stamp>? = null
    var myImageAdapter: MyStampAdapter? = null

    var mySavedImageAdapter: MyStampAdapter? = null
    var stampListSaved: List<Stamp>? = null
    var mySaveImg: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid) {
            view.edit_btn.text = "Manage Account"

        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowing()
        }

        //recycle view for upload stamps
        var recyclerViewUploadImages: RecyclerView
        recyclerViewUploadImages = view.findViewById(R.id.recycler_view_grid)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages.layoutManager = linearLayoutManager

        stampList = ArrayList()
        myImageAdapter = context?.let { MyStampAdapter(it, stampList as ArrayList<Stamp>) }
        recyclerViewUploadImages.adapter = myImageAdapter

        //recycle view for saved stamps
        var recyclerViewSavedImages: RecyclerView
        recyclerViewSavedImages = view.findViewById(R.id.recycler_view_saved)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewSavedImages.layoutManager = linearLayoutManager2

        stampListSaved = ArrayList()
        mySavedImageAdapter = context?.let { MyStampAdapter(it, stampListSaved as ArrayList<Stamp>) }
        recyclerViewSavedImages.adapter = mySavedImageAdapter

        recyclerViewSavedImages.visibility = View.GONE
        recyclerViewUploadImages.visibility = View.VISIBLE

        var uploadedStampsBtn: ImageButton
        uploadedStampsBtn = view.findViewById(R.id.grid_btn)
        uploadedStampsBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewUploadImages.visibility = View.VISIBLE
        }

        var savedStampsBtn: ImageButton
        savedStampsBtn = view.findViewById(R.id.saved_btn)
        savedStampsBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.VISIBLE
            recyclerViewUploadImages.visibility = View.GONE
        }

        view.edit_btn.setOnClickListener {
            val getButtonText = view.edit_btn.text.toString()
            Log.e("Tag",getButtonText)
            when (getButtonText){
                 "Manage Account" -> startActivity(Intent(context, AccountSettingActivity::class.java))

                 "Follow" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(it1.toString())
                                .child("Following").child(profileId)
                                .setValue(true)
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(profileId)
                                .child("Followers").child(it1.toString())
                                .setValue(true)
                    }

                    addNotification()
                }

                "Following" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(it1.toString())
                                .child("Following").child(profileId)
                                .removeValue()
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(profileId)
                                .child("Followers").child(it1.toString())
                                .removeValue()
                    }
                }


            }
        }

        view.followers_layout.setOnClickListener{
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }

        view.following_layout.setOnClickListener{
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }

        getFollowers()
        getFollowing()
        userInfo()
        myStamps()
        getTotalNumberOfStamps()
        mySaves()

        return view
    }

    private fun addNotification(){
        val notifRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(profileId)

        val notifMap = HashMap<String, Any>()
        notifMap["userid"] = firebaseUser!!.uid
        notifMap["desctext"] = "started following you"
        notifMap["stampid"] = ""
        notifMap["ispost"] = false

        notifRef.push().setValue(notifMap)
    }

    private fun mySaves() {
        mySaveImg = ArrayList()
        val savedRef = FirebaseDatabase.getInstance()
            .reference.child("Saves")
            .child(firebaseUser.uid)

        savedRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (p in snapshot.children){
                        (mySaveImg as ArrayList<String>).add(p.key!!)
                    }

                    readSavedImgData()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun readSavedImgData() {
        val stampRef = FirebaseDatabase.getInstance().reference.child("Stamps")

        stampRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (stampListSaved as ArrayList<Stamp>).clear()

                    for (p in snapshot.children){
                        val stamp = p.getValue(Stamp::class.java)

                        for (key in mySaveImg!!){
                            if (stamp!!.stampid == key){
                                (stampListSaved as ArrayList<Stamp>).add(stamp!!)
                            }
                        }
                    }

                    mySavedImageAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edit_btn.setOnClickListener {
            startActivity(Intent(context, AccountSettingActivity::class.java))
        }
        val pref=Pref(context!!)
        profile_username.text = pref.userUsername
        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/stampapps-fcc73.appspot.com/o/Default%20Image%2Fbaby.png?alt=media&token=f975880d-3964-459c-974a-670c8dd04410")
                .fit()
                .into(img_profile)
    }

    private fun myStamps(){
        val stampRef = FirebaseDatabase.getInstance().reference.child("Stamps")

        stampRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (stampList as ArrayList<Stamp>).clear()

                    for (p in snapshot.children){
                        val stamp = p.getValue(Stamp::class.java)
                        if (stamp?.publisher.equals(profileId)){
                            stamp?.let { (stampList as ArrayList<Stamp>).add(it) }
                        }
                        Collections.reverse(stampList)
                        myImageAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkFollowAndFollowing() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }

        if (followingRef != null){
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()){
                        view?.edit_btn?.text = "Following"
                    }//end if
                    else{
                        view?.edit_btn?.text = "Follow"
                    }
                }//end data change

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun getFollowers(){
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    view?.total_followers?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowing(){
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Following")



        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    view?.total_following?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    private fun userInfo(){
        var usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                if(context != null){
//                    return
//                }

                if(snapshot.exists()){
                    val item = snapshot.getValue<Item>(Item::class.java)

                    Picasso.get().load(item!!.getImage()).placeholder(R.drawable.ic_profile).into(view?.img_profile)
                    view?.profile_username?.text = item!!.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    private fun getTotalNumberOfStamps(){
        val stampRef = FirebaseDatabase.getInstance().reference.child("Stamps")

        stampRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var stampCounter = 0

                    for (p in snapshot.children){
                        var stamp = p.getValue(Stamp::class.java)!!

                        if (stamp.publisher == profileId){
                            stampCounter++
                        }
                    }
                    total_stamp.text = " " + stampCounter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}