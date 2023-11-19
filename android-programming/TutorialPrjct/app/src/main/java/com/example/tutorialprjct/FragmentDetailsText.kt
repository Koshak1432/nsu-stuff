package com.example.tutorialprjct

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class FragmentDetailsText : Fragment() {
    // аналогичен статическим методам и свойствам в Java
    private val viewModel: MyViewModel by activityViewModels()

    companion object {
        private const val ARG_TEXT = "text"

        fun create(text: String?): FragmentDetailsText {
            val fragment = FragmentDetailsText()
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
        val view = inflater.inflate(R.layout.fragment_details_text, container, false)
        println("text, fragments: ${parentFragmentManager.fragments}")
        
        println("text, is visible: ${this.isVisible}, isHidden: ${this.isHidden}," +
                " isInLayout: ${this.isInLayout}, isDetached: ${this.isDetached}," +
                " isAdded: ${this.isAdded}, isResumed: ${this.isResumed}")
        viewModel.currentCounter.value = parentFragmentManager.fragments.size
        val textView = view.findViewById<TextView>(R.id.text)
        textView.text = requireArguments().getString(ARG_TEXT)
        return view
    }
}