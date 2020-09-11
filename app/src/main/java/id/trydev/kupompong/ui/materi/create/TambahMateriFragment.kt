package id.trydev.kupompong.ui.materi.create

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.trydev.kupompong.R

class TambahMateriFragment : Fragment() {

    companion object {
        fun newInstance() = TambahMateriFragment()
    }

    private lateinit var viewModel: TambahMateriViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tambah_materi, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TambahMateriViewModel::class.java)
        // TODO: Use the ViewModel
    }

}