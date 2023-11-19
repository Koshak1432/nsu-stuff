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

    companion object {
        private const val BACK_VISIBLE = "backButton"

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
        }

        val button1 = view.findViewById<Button>(R.id.button_1)
        val button2 = view.findViewById<Button>(R.id.button_2)
        val button3 = view.findViewById<Button>(R.id.button_3)

        button1.setOnClickListener {
            showDetailsButton(button1.text.toString())
        }
        button2.setOnClickListener {
            showDetailsButton(button2.text.toString())
        }
        button3.setOnClickListener {
            showDetailsButton(button3.text.toString())
        }

        return view;
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
            .replace(containerId, FragmentDetailsButton.create(buttonText))
            .addToBackStack("bla")
            .commit()
    }

}