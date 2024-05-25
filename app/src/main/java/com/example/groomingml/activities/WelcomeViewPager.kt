package com.example.groomingml.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.groomingml.R
import com.example.groomingml.utils.ViewPagerAdapter
import me.relex.circleindicator.CircleIndicator3

class WelcomeViewPager: AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var circleIndicator: CircleIndicator3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager_welcome)

        // initializing variables
        // of below line with their id.
        val button = findViewById<Button>(R.id.button)
        viewPager = findViewById(R.id.idViewPager)
        viewPagerAdapter = ViewPagerAdapter()

        // on below line we are setting
        // adapter to our view pager.
        viewPager.adapter = viewPagerAdapter
        circleIndicator = findViewById(R.id.circleIndicator)
        circleIndicator.setViewPager(viewPager)

        button.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}