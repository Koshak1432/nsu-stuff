package com.example.tutorialprjct

import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), OnVisibilityChangeListener {
    private lateinit var fragmentCounterText: TextView
    private val viewModel: MyViewModel by viewModels()

    override fun onVisibilityChange(isVisible: Boolean) {
        findViewById<FragmentContainerView>(R.id.container_list).visibility = if (isVisible) {
            println("CONTAINER LIST IS VISIBLE")
            View.VISIBLE
        } else {
            println("CONTAINER LIST IS GONE")
            View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentCounterText = findViewById(R.id.fragment_counter)
        viewModel.currentCounter.observe(this) {
            fragmentCounterText.text = "Фрагменты: ${it.toString()}"
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backStackEntryCount = supportFragmentManager.backStackEntryCount
                if (backStackEntryCount > 0) {
                    if (supportFragmentManager.getBackStackEntryAt(backStackEntryCount - 1).name == "back") {
                        viewModel.setTextShow(false)
                        println("ACTIVITY: SET TEXT SHOW TO FALSE")
                    }
                    if (backStackEntryCount > 1) {
                        onVisibilityChange(false)
                    } else {
                        onVisibilityChange(true)
                        viewModel.setItemId(null)
                    }
                    println("BACKSTAСK at $backStackEntryCount: ${supportFragmentManager.getBackStackEntryAt(backStackEntryCount - 1).name}")
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
        supportFragmentManager.addOnBackStackChangedListener {
            viewModel.setCounter(supportFragmentManager.fragments.size)
        }

        if (savedInstanceState == null) {
            println("ACTIVITY: savedInstanceState == null, set menu")
            supportFragmentManager.beginTransaction()
                .add(R.id.container_list, FragmentMenu.create(false), "menu")
                .commit()
            viewModel.setCounter(supportFragmentManager.fragments.size)
        }
    }
}