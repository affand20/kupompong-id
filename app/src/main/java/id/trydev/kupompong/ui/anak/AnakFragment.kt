package id.trydev.kupompong.ui.anak

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import id.trydev.kupompong.R

class AnakFragment : Fragment() {

    private lateinit var anakViewModel: AnakViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        anakViewModel =
            ViewModelProviders.of(this).get(AnakViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_anak, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        anakViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}