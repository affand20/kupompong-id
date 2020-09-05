package id.trydev.kupompong.ui.anak.tambah

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.trydev.kupompong.R

class TambahAnakFragment : Fragment() {

    companion object {
        fun newInstance() = TambahAnakFragment()
    }

    private lateinit var viewModel: TambahAnakViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tambah_anak, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TambahAnakViewModel::class.java)
        // TODO: Use the ViewModel
    }

}