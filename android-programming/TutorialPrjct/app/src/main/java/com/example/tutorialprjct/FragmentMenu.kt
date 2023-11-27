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
    private var menuVisibilityListener: OnVisibilityChangeListener? = null

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnVisibilityChangeListener) {
            menuVisibilityListener = context
        } else {
            throw RuntimeException("$context must implement OnVisibilityChangeListener")
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
//        updateFragmentCounter()

        val button1 = view.findViewById<Button>(R.id.button_1)
        val button2 = view.findViewById<Button>(R.id.button_2)
        val button3 = view.findViewById<Button>(R.id.button_3)

        button1.setOnClickListener {
            viewModel.setItemId(R.id.button_1)
            viewModel.setTextShow(false)
            showDetailsButton(button1.text.toString())
        }
        button2.setOnClickListener {
            viewModel.setItemId(R.id.button_1)
            viewModel.setTextShow(false)
            showDetailsButton(button2.text.toString())
        }
        button3.setOnClickListener {
            viewModel.setItemId(R.id.button_1)
            viewModel.setTextShow(false)
            showDetailsButton(button3.text.toString())
        }
        return view
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        updateFragmentCounter()
//    }

    private fun showDetailsButton(buttonText: String) {
        // todo контейнера 2 и класть надо в один и тот же всегда конкретные фрагменты,
        // один должен перекрывать другой, либо один должен быть gone при перевороте
        println("ShowDetailsbutton: is container list visible ${resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE}")
        menuVisibilityListener?.onVisibilityChange(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        parentFragmentManager.popBackStack("bla", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container_item, FragmentDetailsButton.create(buttonText), "button")
            .addToBackStack("bla")
            .commit()
    }

//    private fun updateFragmentCounter() {
//        println("MENU: counter(filtered) : ${parentFragmentManager.fragments.filter { it.isVisible }.size}")
//        println("MENU: counter : ${parentFragmentManager.fragments.size}")
//        for (frag in parentFragmentManager.fragments) {
//            println("TAG: ${frag.tag}, isVisible: ${frag.isVisible}")
//        }
//        viewModel.setCounter(parentFragmentManager.fragments.size)
//    }
}