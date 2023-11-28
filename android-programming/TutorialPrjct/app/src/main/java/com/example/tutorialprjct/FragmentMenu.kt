package com.example.tutorialprjct

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import java.lang.RuntimeException

class FragmentMenu() : Fragment() {
    private val viewModel: MyViewModel by activityViewModels()

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

        val button1 = view.findViewById<Button>(R.id.button_1)
        val button2 = view.findViewById<Button>(R.id.button_2)
        val button3 = view.findViewById<Button>(R.id.button_3)

        button1.setOnClickListener {
            viewModel.setTextShow(false)
            showDetailsButton(button1.text.toString())
        }
        button2.setOnClickListener {
            viewModel.setTextShow(false)
            showDetailsButton(button2.text.toString())
        }
        button3.setOnClickListener {
            viewModel.setTextShow(false)
            showDetailsButton(button3.text.toString())
        }
        return view
    }

    private fun showDetailsButton(buttonText: String) {
        parentFragmentManager.popBackStack("bla", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container_item, FragmentDetailsButton.create(buttonText), "button")
            .addToBackStack("bla")
            .commit()
    }
}