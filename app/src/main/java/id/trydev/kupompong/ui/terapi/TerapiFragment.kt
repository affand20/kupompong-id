package id.trydev.kupompong.ui.terapi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import id.trydev.kupompong.R
import kotlinx.android.synthetic.main.fragment_terapi.*

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


        cv_anak.setOnClickListener{
            val builder = AlertDialog.Builder(requireActivity(), R.style.RoundAlertDialog)
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pilih_anak, null)
//            val btnSubmit = dialogView.findViewById<Button>(R.id.btn_submit)
//            val edtJudulMateri = dialogView.findViewById<EditText>(R.id.edt_judul_materi)
//            val progressBar = dialogView.findViewById<ProgressBar>(R.id.progress_bar)

            builder.setView(dialogView)
            val alertDialog = builder.create()

            val scale = resources.displayMetrics.density
            alertDialog.window?.setLayout((120 * scale).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)

            alertDialog.show()
        }

        tv_anak.setOnClickListener {

        }

    }
}