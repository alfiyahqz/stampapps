package com.alfiyah.stamp

import android.content.Context
import android.os.Bundle
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


/**
 * A simple [Fragment] subclass.
 * Use the [StampDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StampDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var stampAdapter: StampAdapter? = null
    private var stampList: MutableList<Stamp>? = null
    private var stampId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_stamp_details, container, false)

        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (preferences != null){
            stampId = preferences.getString("stampId", "none").toString()
        }

        var recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_stamp_detail)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        stampList = ArrayList()
        stampAdapter = context?.let { StampAdapter(it, stampList as ArrayList<Stamp>) }
        recyclerView.adapter = stampAdapter

        retrievedStamps()

        return view
    }

    private fun retrievedStamps() {
        val stampRef = FirebaseDatabase.getInstance().reference
            .child("Stamps")
            .child(stampId)

        stampRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                stampList?.clear()
                val stamp = snapshot.getValue(Stamp::class.java)
                stampList!!.add(stamp!!)
                stampAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StampDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StampDetailsFragment().apply {

            }
    }
}