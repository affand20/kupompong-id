package id.trydev.kupompong.ui.terapi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.trydev.kupompong.R

class TerapiFragment : Fragment() {

    private lateinit var terapiViewModel: TerapiViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        terapiViewModel =
//            ViewModelProviders.of(this).get(TerapiViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_terapi, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        terapiViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}