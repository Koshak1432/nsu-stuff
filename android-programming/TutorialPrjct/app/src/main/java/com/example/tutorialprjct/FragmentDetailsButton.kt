package com.example.tutorialprjct

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class FragmentDetailsButton() : Fragment() {
    // аналогичен статическим методам и свойствам в Java
    private val viewModel: MyViewModel by activityViewModels()
    private var isTextShown = false

    companion object {
        private const val ARG_IS_TEXT_SHOWN = "textShown"
        private const val ARG_BUTTON_TEXT = "text"

        fun create(text: String): FragmentDetailsButton {
            val fragment = FragmentDetailsButton()
            val args = Bundle()
            args.putString(ARG_BUTTON_TEXT, text)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details_button, container, false)
        val button = view.findViewById<Button>(R.id.button)
        val text = requireArguments().getString(ARG_BUTTON_TEXT)
        button.text = text

        if (savedInstanceState != null) {
            val isTextShown = savedInstanceState.getBoolean(ARG_IS_TEXT_SHOWN)
            println("BUTTON: savedInstanceState != null, isTextShown $isTextShown")
            if (isTextShown) {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    println("BUTTON: savedInstanceState != null, create just text $text")
                    createText(text)
                } else {
                    println("BUTTON: savedInstanceState != null, create menuBack and text $text")
                    createTextAndMenuBack(text)
                }
            }
        }

//        println("button, fragments: ${parentFragmentManager.fragments}")
//
//        println("button, is visible: ${this.isVisible}, isHidden: ${this.isHidden}," +
//                " isInLayout: ${this.isInLayout}, isDetached: ${this.isDetached}," +
//                " isAdded: ${this.isAdded}, isResumed: ${this.isResumed}")


        updateFragmentCounter()
        setListeners(button, text)
        return view
    }

    private fun setListeners(button: Button, text: String?) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            button.setOnClickListener {
                createText(text)
            }
        } else {
            button.setOnClickListener {
                createTextAndMenuBack(text)
            }
        }
    }

    private fun createTextAndMenuBack(text: String?) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container_item, FragmentDetailsText.create(text), "text")
            .replace(R.id.container_list, FragmentMenu.create(true), "menuBack")
            .addToBackStack("back")
            .commit()
        isTextShown = true
    }

    private fun createText(text: String?) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container_list, FragmentDetailsText.create(text), "text")
            .addToBackStack("back")
            .commit()
        isTextShown = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ARG_IS_TEXT_SHOWN, isTextShown)

        println("BUTTON: save instance, isTextShown: $isTextShown")
    }

    private fun updateFragmentCounter() {
        viewModel.currentCounter.value = parentFragmentManager.fragments.size
//        println("button, size: ${parentFragmentManager.fragments.size}")
    }
}