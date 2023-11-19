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
//    private var counter = 0
//    private val counterKey = "counter"

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val btnIncrement = findViewById<Button>(R.id.incrementBtn)
//        val btnShow = findViewById<Button>(R.id.showToast)
//        btnIncrement.setOnClickListener {
//            counter++
//        }
//        btnShow.setOnClickListener {
//            val counterVal = "Counter value: $counter"
//            Toast.makeText(this, counterVal, Toast.LENGTH_SHORT).show()
//        }
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.run {
//            putInt(counterKey, counter)
//        }
//        super.onSaveInstanceState(outState)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        counter = savedInstanceState.getInt(counterKey)
//    }

    private lateinit var fragmentCounterText: TextView

//    private lateinit var viewModel: MyViewModel

    private val viewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentCounterText = findViewById(R.id.fragment_counter)
//        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        viewModel.currentCounter.observe(this) {
            fragmentCounterText.text = "Фрагменты: ${it.toString()}"
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
//                updateFragmentCounter()
            }
        })

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container_list, FragmentMenu.create(false), "menu")
                .commit()
        }
//        updateFragmentCounter()
    }

    private fun updateFragmentCounter() {
        val visibleFragments = supportFragmentManager.fragments
//        fragmentCounterText.text = "Фрагментыы: ${visibleFragments.size}"
    }
}