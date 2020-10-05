package id.trydev.kupompong.ui.materi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.MateriAdapter
import id.trydev.kupompong.model.Materi
import id.trydev.kupompong.ui.materi.tambah.TambahMateriActivity
import kotlinx.android.synthetic.main.fragment_materi.*
import kotlinx.android.synthetic.main.fragment_materi.fab_add
import kotlinx.android.synthetic.main.fragment_materi.state_empty
import java.util.*

class MateriFragment : Fragment() {

    private lateinit var adapter: MateriAdapter
    private val mFirestore = FirebaseFirestore.getInstance()

    private val listMateri = mutableListOf<Materi>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_materi, container, false)
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

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val listFiltered = listMateri.filter {
                    it.judul.toString().toLowerCase(Locale.getDefault()).contains(p0.toString().toLowerCase(Locale.getDefault()))
                }

                Log.d("FILTERED", "$listFiltered")

                adapter.setData(listFiltered)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


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
                this.listMateri.clear()
                listMateri.addAll(it.toObjects(Materi::class.java))
                Log.d("LOAD DATA", "$listMateri")
                if (listMateri.size > 0) {
                    adapter.setData(listMateri)
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