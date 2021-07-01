package com.sheiii.app.view.four

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sheiii.app.R

class FourFragment : Fragment() {

    private lateinit var fourViewModel: FourViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fourViewModel = ViewModelProvider(this).get(FourViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_four, container, false)
        val textView: TextView = root.findViewById(R.id.text_four)
        fourViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })
        return root
    }
}