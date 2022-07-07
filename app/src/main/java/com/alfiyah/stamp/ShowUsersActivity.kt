package com.alfiyah.stamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowUsersActivity : AppCompatActivity() {

    var id: String = ""
    var title: String = ""

    var itemAdapter: ItemAdapter? = null
    var itemList: List<Item>? = null
    var idList: List<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)

        val intent = intent
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_user)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        var recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recycler_view_show_user)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemList = ArrayList()
        itemAdapter = ItemAdapter(this, itemList as ArrayList<Item>, false)
        recyclerView.adapter = itemAdapter

        idList = ArrayList()

        when(title){
            "likes" -> getLikes()
            "following" -> getFollowing()
            "followers" -> getFollowers()
            "views" -> getViews()
        }

    }

    private fun getViews() {
        TODO("Not yet implemented")
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id!!)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()

                for (p in snapshot.children){
                    (idList as ArrayList<String>).add(p.key!!)
                }

                showUser()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowing() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id!!)
            .child("Following")



        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()

                for (p in snapshot.children){
                    (idList as ArrayList<String>).add(p.key!!)
                }

                showUser()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getLikes() {
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(id!!)
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (idList as ArrayList<String>).clear()

                    for (p in snapshot.children){
                        (idList as ArrayList<String>).add(p.key!!)
                    }

                    showUser()
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showUser() {
        val itemRef = FirebaseDatabase.getInstance().getReference().child("Users")
        itemRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                (itemList as ArrayList<Item>).clear()

                for(snapshot in dataSnapshot.children){
                    val item = snapshot.getValue(Item::class.java)

                    for (id in idList!!){
                        if (item!!.getUid() == id){
                            (itemList as ArrayList<Item>).add(item!!)
                        }
                    }
                }//end for

                itemAdapter?.notifyDataSetChanged()
            }//end onDataChange

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setSupportActionBar(toolbar: Toolbar) {

    }
}