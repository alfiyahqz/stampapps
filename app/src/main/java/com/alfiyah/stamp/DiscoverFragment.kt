package com.alfiyah.stamp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_discover.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiscoverFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiscoverFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var itemAdapter: ItemAdapter? = null
    private var mItem: MutableList<Item>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_discover, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mItem = ArrayList()
        itemAdapter = context?.let {ItemAdapter(it, mItem as ArrayList<Item>, true)}
        recyclerView?.adapter = itemAdapter

        recyclerView?.visibility = View.GONE


        view.search_txt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(view.search_txt.toString() == ""){
                    recyclerView?.visibility = View.GONE
                } else{
                    recyclerView?.visibility = View.VISIBLE

                    retrievedItems()
                    searchItem(s.toString().toLowerCase())

                }
            } //end of onTextChanged

            override fun afterTextChanged(s: Editable?) {

            }
        })


        return view
    }

    private fun searchItem(input: String) {
        val query = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("username")
            .startAt(input)
            .endAt(input + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mItem?.clear()
                for(snapshot in dataSnapshot.children){
                    val item = snapshot.getValue(Item::class.java)
                    if(item != null){
                        mItem?.add(item)
                    }
                }//end for

                itemAdapter?.notifyDataSetChanged()
            }//end onDataChange

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun retrievedItems() {
        val itemRef = FirebaseDatabase.getInstance().getReference().child("Users")
        itemRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(view?.search_txt?.text.toString() == ""){
                    mItem?.clear()

                    for(snapshot in dataSnapshot.children){
                        val item = snapshot.getValue(Item::class.java)
                        if(item != null){
                            mItem?.add(item)
                        }
                    }//end for

                    itemAdapter?.notifyDataSetChanged()

                }//end if
            }//end onDataChange

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
