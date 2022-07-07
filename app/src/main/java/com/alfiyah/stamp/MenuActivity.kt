package com.alfiyah.stamp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_menu.*


class MenuActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val discoverFragment = DiscoverFragment()
//    private val stampFragment = AddStampFragment()
    private val notificationsFragment = NotificationsFragment()
    private val profileFragment = ProfileFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        replaceFragment(homeFragment)
        bottom_nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(homeFragment)
                R.id.discover -> replaceFragment(discoverFragment)

                R.id.add -> {
                    startActivity(Intent(this@MenuActivity, AddStampActivity::class.java))

                }
                R.id.notifications -> replaceFragment(notificationsFragment)
                R.id.profile -> replaceFragment(profileFragment)

            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}