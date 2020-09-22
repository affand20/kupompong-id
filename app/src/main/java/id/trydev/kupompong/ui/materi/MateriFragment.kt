package id.trydev.kupompong.ui.materi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import id.trydev.kupompong.R
import id.trydev.kupompong.ui.materi.tambah.TambahMateriActivity
import kotlinx.android.synthetic.main.fragment_materi.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_add.setOnClickListener {
            startActivity(
                Intent(requireContext(), TambahMateriActivity::class.java)
            )
        }
    }
}