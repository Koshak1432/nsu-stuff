package com.example.tutorialprjct

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.OrientationHelper

class FragmentDetailsButton() : Fragment() {
    // аналогичен статическим методам и свойствам в Java
    companion object {
        private const val ARG_TEXT = "text"
        fun create(text: String): FragmentDetailsButton {
            val fragment = FragmentDetailsButton()
            val args = Bundle()
            args.putString(ARG_TEXT, text)
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
        val text = requireArguments().getString(ARG_TEXT)
        button.text = text

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            button.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container_list, FragmentDetailsText.create(text))
                    .addToBackStack("back")
                    .commit()
            }
        } else {
            button.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container_item, FragmentDetailsText.create(text))
                    .replace(R.id.container_list, FragmentMenu.create(true))
                    .addToBackStack("back")
                    .commit()
            }
        }
        return view
    }


}