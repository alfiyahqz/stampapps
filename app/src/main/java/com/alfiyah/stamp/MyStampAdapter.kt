package com.alfiyah.stamp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyStampAdapter(private val mContext: Context, mStamp: List<Stamp>): RecyclerView.Adapter<MyStampAdapter.ViewHolder?>() {

    private var mStamp: List<Stamp>? = null

    init {
        this.mStamp = mStamp
    }

    inner class ViewHolder(@NonNull imgView: View): RecyclerView.ViewHolder(imgView){
        var stampImage: ImageView

        init {
            stampImage = imgView.findViewById(R.id.stamp_image_item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.images_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stamp: Stamp = mStamp!![position]

        Picasso.get().load(stamp.stampimage).into(holder.stampImage)

        holder.stampImage.setOnClickListener{
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("stampId", stamp.stampid)
            editor.apply()
            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, StampDetailsFragment()).commit()
        }
    }

    override fun getItemCount(): Int {
        return mStamp!!.size
    }
}