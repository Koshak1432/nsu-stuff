package com.example.tutorialprjct

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels

class FragmentMenu() : Fragment() {
    private val viewModel: MyViewModel by activityViewModels()
    private var curItemId: Int = 0

    companion object {
        private const val BACK_VISIBLE = "backButton"
        const val ARG_ITEM_ID = "itemId"

        fun create(isBackVisible: Boolean): FragmentMenu {
            val menu = FragmentMenu()
            val args = Bundle()
            args.putBoolean(BACK_VISIBLE, isBackVisible)
            menu.arguments = args
            return menu
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        val isBackButtonVisible = requireArguments().getBoolean(BACK_VISIBLE)
        val backButton = view.findViewById<Button>(R.id.back_button)
        backButton.visibility = if (isBackButtonVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack("back", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            viewModel.setTextShow(false)
        }
//        println("menu, is visible: ${this.isVisible}, isHidden: ${this.isHidden}," +
//                " isInLayout: ${this.isInLayout}, isDetached: ${this.isDetached}," +
//                " isAdded: ${this.isAdded}, isResumed: ${this.isResumed}")
        updateFragmentCounter()
//        println("menu, size: ${parentFragmentManager.fragments.size}")

        val button1 = view.findViewById<Button>(R.id.button_1)
        val button2 = view.findViewById<Button>(R.id.button_2)
        val button3 = view.findViewById<Button>(R.id.button_3)

        button1.setOnClickListener {
            curItemId = R.id.button_1
            showDetailsButton(button1.text.toString())
        }
        button2.setOnClickListener {
            curItemId = R.id.button_2
            showDetailsButton(button2.text.toString())
        }
        button3.setOnClickListener {
            curItemId = R.id.button_3
            showDetailsButton(button3.text.toString())
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            curItemId = savedInstanceState.getInt(ARG_ITEM_ID, 0)
            println("MENU: onCreated, savedInstanceState != null, curItemId: $curItemId")
            println(savedInstanceState.describeContents())
        }
        if (curItemId != 0) {
            showDetailsButton(resources.getResourceEntryName(curItemId))
            println("MENU: onCreate getResNameBy id $curItemId")
        }
    }

    private fun showDetailsButton(buttonText: String) {
        val containerId =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                R.id.container_list
            } else {
                R.id.container_item
            }
        parentFragmentManager.popBackStack("bla", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        parentFragmentManager.beginTransaction()
            .replace(containerId, FragmentDetailsButton.create(buttonText), "button")
            .addToBackStack("bla")
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_ITEM_ID, curItemId)
        println("MENU: save instance, curItemId: $curItemId")
    }

    private fun updateFragmentCounter() {
        viewModel.updateCounter(parentFragmentManager.fragments.size)
    }
}