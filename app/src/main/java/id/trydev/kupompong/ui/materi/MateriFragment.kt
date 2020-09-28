package id.trydev.kupompong.ui.materi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.MateriAdapter
import id.trydev.kupompong.model.Materi
import id.trydev.kupompong.ui.materi.tambah.TambahMateriActivity
import kotlinx.android.synthetic.main.dialog_tambah_materi.*
import kotlinx.android.synthetic.main.fragment_materi.*
import kotlinx.android.synthetic.main.fragment_materi.fab_add
import kotlinx.android.synthetic.main.fragment_materi.state_empty

class MateriFragment : Fragment() {

    private lateinit var materiViewModel: MateriViewModel

    private lateinit var adapter: MateriAdapter
    private val mFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_materi, container, false)
        return root
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MateriAdapter(requireContext()) {
            startActivity(
                Intent(
                    requireContext(), TambahMateriActivity::class.java
                ).putExtra("materiId", it.id)
                    .putExtra("judul", it.judul)
            )
        }

        rv_materi.layoutManager = GridLayoutManager(requireContext(), 2)
        rv_materi.adapter = adapter

        swipe_refresh.setOnRefreshListener {
            getData()
        }


        val builder = AlertDialog.Builder(requireActivity(), R.style.RoundAlertDialog)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_tambah_materi, null)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btn_submit)
        val edtJudulMateri = dialogView.findViewById<EditText>(R.id.edt_judul_materi)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progress_bar)

        builder.setView(dialogView)
        val alertDialog = builder.create()

        val scale = resources.displayMetrics.density
        alertDialog.window?.setLayout((200 * scale).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)

        fab_add.setOnClickListener {
            edtJudulMateri.text.clear()
            alertDialog.show()
        }

        fun validate(): Boolean {
            if (edtJudulMateri.text.isEmpty()) {
                edtJudulMateri.error = "Wajib diisi"
                edtJudulMateri.requestFocus()
                return false
            }
            return true
        }

        fun showLoading() {
            btnSubmit.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        }

        fun hideLoading() {
            btnSubmit.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
        }

        btnSubmit.setOnClickListener {
            if (validate()) {
                val path = mFirestore.collection("materi")
                val key = path.document().id
                val materi = Materi(key, edtJudulMateri.text.toString())

                Log.d("PATH", "$path, $key")

                showLoading()

                path.document(key).set(materi)
                    .addOnSuccessListener {
                        hideLoading()
                        alertDialog.dismiss()
                        startActivity(
                            Intent(
                                requireContext(), TambahMateriActivity::class.java
                            ).putExtra("materiId", key)
                                .putExtra("judul", edtJudulMateri.text.toString())
                        )
                    }
                    .addOnFailureListener {
                        alertDialog.dismiss()
                        hideLoading()
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun getData() {
        swipe_refresh.isRefreshing = true
        rv_materi.visibility = View.GONE

        mFirestore.collection("materi")
            .get()
            .addOnSuccessListener {
                swipe_refresh.isRefreshing = false
                val listData = mutableListOf<Materi>()
                listData.addAll(it.toObjects(Materi::class.java))
                Log.d("LOAD DATA", "$listData")
                if (listData.size > 0) {
                    adapter.setData(listData)
                    rv_materi.visibility = View.VISIBLE
                    state_empty.visibility = View.GONE
                } else {
                    rv_materi.visibility = View.GONE
                    state_empty.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                swipe_refresh.isRefreshing = false
                rv_materi.visibility = View.GONE
                state_empty.visibility = View.VISIBLE
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

}