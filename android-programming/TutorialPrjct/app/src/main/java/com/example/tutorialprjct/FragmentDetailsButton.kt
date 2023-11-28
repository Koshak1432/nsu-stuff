package com.example.tutorialprjct

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.lang.RuntimeException

class FragmentDetailsButton : Fragment() {
    // аналогичен статическим методам и свойствам в Java
    private val viewModel: MyViewModel by activityViewModels()
    private var menuVisibilityListener: OnVisibilityChangeListener? = null

    companion object {
        private const val ARG_BUTTON_TEXT = "text"

        fun create(text: String): FragmentDetailsButton {
            val fragment = FragmentDetailsButton()
            val args = Bundle()
            args.putString(ARG_BUTTON_TEXT, text)
            fragment.arguments = args
            return fragment
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
        val view = inflater.inflate(R.layout.fragment_details_button, container, false)
        val button = view.findViewById<Button>(R.id.button)
        val text = requireArguments().getString(ARG_BUTTON_TEXT)
        button.text = text
        button.setOnClickListener {
            createText(text)
        }
        menuVisibilityListener?.onVisibilityChange(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = requireArguments().getString(ARG_BUTTON_TEXT)
        if (viewModel.isTextShow()) {
            parentFragmentManager.popBackStack()
            createText(text)
        }
    }

    private fun createText(text: String?) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_item, FragmentDetailsText.create(text), "text")
                .addToBackStack("back")
                .commit()
        } else {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_item, FragmentDetailsText.create(text), "text")
                .replace(R.id.container_list, FragmentMenu.create(true), "menuBack")
                .addToBackStack("back")
                .commit()
        }
        viewModel.setTextShow(true)
    }
}