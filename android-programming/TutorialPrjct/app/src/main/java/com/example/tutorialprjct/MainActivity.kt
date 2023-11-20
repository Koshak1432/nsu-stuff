package com.example.tutorialprjct

import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private val ARG_ITEM_ID = "itemId"
    private val ARG_PORTRAIT = "port"
    private val ARG_FRAGMENTS = "fragments"

    private lateinit var fragmentCounterText: TextView
    private val viewModel: MyViewModel by viewModels()

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.run {
//            putBoolean(ARG_PORTRAIT, resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
//            putIntArray(ARG_FRAGMENTS, supportFragmentManager.fragments.map{it.id}.toIntArray())
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentCounterText = findViewById(R.id.fragment_counter)
        viewModel.currentCounter.observe(this) {
            fragmentCounterText.text = "Фрагменты: ${it.toString()}"
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
//                    println("backStackCount: ${supportFragmentManager.backStackEntryCount}")
//                    println("activity, fragments: ${supportFragmentManager.fragments}")
//
//                    for (i in supportFragmentManager.fragments.filter { it.isVisible }) {
//                        println("${i.tag}, is visible: ${i.isVisible}, isHidden: ${i.isHidden}," +
//                                " isInLayout: ${i.isInLayout}, isDetached: ${i.isDetached}," +
//                                " isAdded: ${i.isAdded}, isResumed: ${i.isResumed}")
//                    }
                } else {
                    finish()
                }
            }
        })

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container_list, FragmentMenu.create(false), "menu")
                .commit()
            println("ACTIVITY: savedInstanceState == null, set menu")
        }
    }
}