package com.example.tutorialprjct

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.OrientationHelper

class FragmentDetailsButton() : Fragment() {
    // аналогичен статическим методам и свойствам в Java
    private val viewModel: MyViewModel by activityViewModels()

    companion object {
        private const val ARG_TEXT = "text"
        private const val ARG_ITEM = "itemId"

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
        println("button, fragments: ${parentFragmentManager.fragments}")

        println("button, is visible: ${this.isVisible}, isHidden: ${this.isHidden}," +
                " isInLayout: ${this.isInLayout}, isDetached: ${this.isDetached}," +
                " isAdded: ${this.isAdded}, isResumed: ${this.isResumed}")


        updateFragmentCounter()

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            button.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container_list, FragmentDetailsText.create(text), "text")
                    .addToBackStack("back")
                    .commit()
            }
        } else {
            button.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container_item, FragmentDetailsText.create(text), "text")
                    .replace(R.id.container_list, FragmentMenu.create(true), "menuBack")
                    .addToBackStack("back")
                    .commit()
            }
        }
        return view
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.run {
//            putInt(ARG_ITEM, id)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val buttonId = savedInstanceState?.getInt(ARG_ITEM)
//    }

    private fun updateFragmentCounter() {
        viewModel.currentCounter.value = parentFragmentManager.fragments.size
        println("button, size: ${parentFragmentManager.fragments.size}")
    }
}