package com.example.tutorialprjct

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.lang.RuntimeException

class FragmentDetailsText : Fragment() {
    // аналогичен статическим методам и свойствам в Java
    private val viewModel: MyViewModel by activityViewModels()
    private var menuVisibilityListener: OnVisibilityChangeListener? = null

    companion object {
        private const val ARG_TEXT = "textViewText"

        fun create(text: String?): FragmentDetailsText {
            val fragment = FragmentDetailsText()
            val args = Bundle()
            args.putString(ARG_TEXT, text)
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
        val view = inflater.inflate(R.layout.fragment_details_text, container, false)
        val textView = view.findViewById<TextView>(R.id.text)
        updateCounter()
        textView.text = requireArguments().getString(ARG_TEXT)
        val menu =
        menuVisibilityListener?.onVisibilityChange(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        println("TEXT with text: ${requireArguments().getString(ARG_TEXT)}")
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        updateCounter()
    }

    private fun updateCounter() {
        val fragments =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                parentFragmentManager.fragments.size
            } else {
                parentFragmentManager.fragments.filter { it.isVisible }.size
            }
        for (frag in parentFragmentManager.fragments) {
            println("TAG: ${frag.tag}, isVisible: ${frag.isVisible}")
        }
        println("TEXT: counter: ${parentFragmentManager.fragments.size}")
        println("TEXT: filtered counter: ${parentFragmentManager.fragments.filter { it.isVisible }.size}")
        println("TEXT: updated counter to : $fragments")
//        viewModel.setCounter(fragments)
        viewModel.setCounter(parentFragmentManager.fragments.size)
    }
}