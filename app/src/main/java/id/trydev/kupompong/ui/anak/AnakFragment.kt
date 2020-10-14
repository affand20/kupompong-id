package id.trydev.kupompong.ui.anak

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.DaftarAnakAdapter
import id.trydev.kupompong.model.Anak
import id.trydev.kupompong.ui.anak.detail.DetailAnakActivity
import id.trydev.kupompong.ui.anak.tambah.TambahAnakActivity
import kotlinx.android.synthetic.main.fragment_anak.*
import kotlinx.android.synthetic.main.fragment_terapi.*

class AnakFragment : Fragment() {

    private lateinit var adapter: DaftarAnakAdapter

    private val mFirestore = FirebaseFirestore.getInstance()
    private val listData = mutableListOf<Anak>()

    private var filterUmurStart: Int? = null
    private var filterUmurEnd: Int? = null
    private val filterGender = Array(2) {""}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_anak, container, false)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DaftarAnakAdapter(requireContext()) {
            startActivity(
                Intent(requireActivity(), DetailAnakActivity::class.java)
                    .putExtra("data_anak", it)
            )
        }

        rv_anak.layoutManager = LinearLayoutManager(requireContext())
        rv_anak.adapter = adapter

        fab_add.setOnClickListener {
            startActivity(Intent(requireActivity(), TambahAnakActivity::class.java))
        }

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val listFiltered = listData.filter {
                    it.fullName.toString().toLowerCase().contains(p0.toString())
                }

                adapter.setData(listFiltered)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        btn_filter.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity(), R.style.RoundAlertDialog)
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter_anak, null)

            val edtOldStart = dialogView.findViewById<EditText>(R.id.edt_old_start)
            val edtOldEnd = dialogView.findViewById<EditText>(R.id.edt_old_end)
            val btnTerapkan = dialogView.findViewById<Button>(R.id.btn_terapkan_filter)
            val cbLk = dialogView.findViewById<CheckBox>(R.id.cb_lk)
            val cbPr = dialogView.findViewById<CheckBox>(R.id.cb_pr)

            if (filterUmurStart != null) {
                edtOldStart.setText(filterUmurStart.toString())
            }

            if (filterUmurEnd != null) {
                edtOldEnd.setText(filterUmurEnd.toString())
            }

            if (filterGender[0] != "") {
                cbLk.isChecked = true
            }

            if (filterGender[1] != "") {
                cbPr.isChecked = true
            }

            fun validate(): Boolean {
                if (edtOldStart.text.isNotEmpty() && (edtOldStart.text.toString().toInt() < 0 || edtOldStart.text.toString().toInt() > 100)) {
                    edtOldStart.requestFocus()
                    edtOldStart.error = "Masukkan nilai antara 0-100"
                    return false
                }
                if (edtOldEnd.text.isNotEmpty() && (edtOldEnd.text.toString().toInt() < 0 || edtOldEnd.text.toString().toInt() > 100)) {
                    edtOldEnd.requestFocus()
                    edtOldEnd.error = "Masukkan nilai antara 0-100"
                    return false
                }

                return true
            }

            builder.setView(dialogView)
            val dialog = builder.create()

            val scale = resources.displayMetrics.density
            dialog.window?.setLayout((.50 * scale).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)

            dialog.show()

            btnTerapkan.setOnClickListener {
                if (validate()) {
                    dialog.dismiss()
                    if (edtOldStart.text.toString().isNotEmpty()) {
                        this.filterUmurStart = edtOldStart.text.toString().toInt()
                    } else {
                        filterUmurStart = null
                    }
                    if (edtOldEnd.text.toString().isNotEmpty()) {
                        this.filterUmurEnd = edtOldEnd.text.toString().toInt()
                    } else {
                        filterUmurEnd = null
                    }
                    if (cbLk.isChecked) {
                        filterGender[0] = cbLk.text.toString()
                    } else {
                        filterGender[0] = ""
                    }
                    if (cbPr.isChecked) {
                        filterGender[1] = cbPr.text.toString()
                    } else {
                        filterGender[1] = ""
                    }
                }
            }

            dialog.setOnDismissListener {
                var listFiltered = listData.toList()

                if (filterUmurStart != null && filterUmurEnd != null) {
                    Log.d("FILTER UMUR", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.old.toString().toInt() in filterUmurStart.toString().toInt()..filterUmurEnd.toString().toInt()
                    }
                }
                else if (filterUmurStart != null) {
                    Log.d("FILTER UMUR START", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.old.toString().toInt() == filterUmurStart.toString().toInt()
                    }
                }

                if (filterGender[0] != "" && filterGender[1] != "") {
                    Log.d("FILTER GENDER", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.gender.toString() == filterGender[0] || it.gender.toString() == filterGender[1]
                    }
                }
                else if (filterGender[0] != "") {
                    Log.d("FILTER GENDER LAKI", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.gender.toString() == filterGender[0]
                    }

                } else if (filterGender[1] != "") {
                    Log.d("FILTER GENDER PEREMPUAN", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.gender.toString() == filterGender[1]
                    }
                }

                if (listFiltered.isNotEmpty()) {
                    adapter.setData(listFiltered)
                    rv_anak.visibility = View.VISIBLE
                    state_empty.visibility = View.GONE
                } else {
                    rv_anak.visibility = View.GONE
                    state_empty.visibility = View.VISIBLE
                }
//                adapter.setData(listFiltered)

            }
        }

        swipe_refresh.setOnRefreshListener {
            loadData()
        }
    }

    private fun showLoading() {
        swipe_refresh.isRefreshing = true
    }

    private fun hideLoading() {
        swipe_refresh.isRefreshing = false
    }

    private fun loadData() {
        rv_anak.visibility = View.GONE
        state_empty.visibility = View.VISIBLE
        showLoading()

        mFirestore.collection("anak")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                hideLoading()
                listData.clear()
                listData.addAll(it.toObjects(Anak::class.java))
                Log.d("LOAD DATA", "$listData")
                if (listData.size > 0) {
                    adapter.setData(listData)
                    rv_anak.visibility = View.VISIBLE
                    state_empty.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                hideLoading()
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    // TODO : Batalkan request ke firebase ketika state fragment tidak aktif
}