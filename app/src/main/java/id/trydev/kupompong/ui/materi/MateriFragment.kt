package id.trydev.kupompong.ui.materi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import id.trydev.kupompong.R

class MateriFragment : Fragment() {

    private lateinit var materiViewModel: MateriViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        materiViewModel =
//            ViewModelProviders.of(this).get(MateriViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_materi, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        materiViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }
}