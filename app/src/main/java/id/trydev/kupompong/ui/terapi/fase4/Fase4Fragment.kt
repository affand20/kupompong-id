package id.trydev.kupompong.ui.terapi.fase4

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.trydev.kupompong.R

class Fase4Fragment : Fragment() {

    companion object {
        fun newInstance() = Fase4Fragment()
    }

    private lateinit var viewModel: Fase4ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fase4, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(Fase4ViewModel::class.java)
        // TODO: Use the ViewModel
    }

}