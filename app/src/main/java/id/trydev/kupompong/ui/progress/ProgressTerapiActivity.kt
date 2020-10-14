package id.trydev.kupompong.ui.progress

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.ProgressTerapiAdapter
import id.trydev.kupompong.model.HasilTerapi
import id.trydev.kupompong.ui.progress.detail.DetailProgressActivity
import kotlinx.android.synthetic.main.activity_progress_terapi.*
import kotlinx.android.synthetic.main.activity_progress_terapi.btn_filter
import kotlinx.android.synthetic.main.activity_progress_terapi.edt_search
import kotlinx.android.synthetic.main.activity_progress_terapi.state_empty
import kotlinx.android.synthetic.main.activity_progress_terapi.swipe_refresh
import kotlinx.android.synthetic.main.fragment_anak.*

class ProgressTerapiActivity : AppCompatActivity() {

    private val mFirestore = FirebaseFirestore.getInstance()

    private lateinit var adapter: ProgressTerapiAdapter
    private val listHasilTerapi = mutableListOf<HasilTerapi>()

    private var filterUmurStart: Int? = null
    private var filterUmurEnd: Int? = null
    private val filterGender = Array(2) {""}

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_terapi)

        supportActionBar?.hide()

        adapter = ProgressTerapiAdapter(this) {
            startActivity(
                Intent(this, DetailProgressActivity::class.java)
                    .putExtra("hasil_terapi", it)
            )
        }

        rv_hasil_terapi.layoutManager = LinearLayoutManager(this)
        rv_hasil_terapi.adapter = adapter

        btn_back.setOnClickListener {
            finish()
        }

        swipe_refresh.setOnRefreshListener {
            getData()
        }

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val listFiltered = listHasilTerapi.filter {
                    it.namaAnak.toString().toLowerCase().contains(p0.toString()) ||
                            it.topik.toString().toLowerCase().contains(p0.toString())
                }

                adapter.setData(listFiltered)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        btn_filter.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.RoundAlertDialog)
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter_anak, null)

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
                var listFiltered = listHasilTerapi.toList()

                if (filterUmurStart != null && filterUmurEnd != null) {
                    Log.d("FILTER UMUR", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.umurAnak.toString().toInt() in filterUmurStart.toString().toInt()..filterUmurEnd.toString().toInt()
                    }
                }
                else if (filterUmurStart != null) {
                    Log.d("FILTER UMUR START", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.umurAnak.toString().toInt() == filterUmurStart.toString().toInt()
                    }
                }

                if (filterGender[0] != "" && filterGender[1] != "") {
                    Log.d("FILTER GENDER", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.genderAnak.toString() == filterGender[0] || it.genderAnak.toString() == filterGender[1]
                    }
                }
                else if (filterGender[0] != "") {
                    Log.d("FILTER GENDER LAKI", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.genderAnak.toString() == filterGender[0]
                    }

                } else if (filterGender[1] != "") {
                    Log.d("FILTER GENDER PEREMPUAN", "MASUK")
                    listFiltered = listFiltered.filter {
                        it.genderAnak.toString() == filterGender[1]
                    }
                }

                if (listFiltered.isNotEmpty()) {
                    adapter.setData(listFiltered)
                    rv_hasil_terapi.visibility = View.VISIBLE
                    state_empty.visibility = View.GONE
                } else {
                    rv_hasil_terapi.visibility = View.GONE
                    state_empty.visibility = View.VISIBLE
                }
//                adapter.setData(listFiltered)

            }
        }

    }

    private fun getData() {
        showLoading()
        mFirestore.collection("hasil_terapi")
            .orderBy("dateTerapi", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                hideLoading()
                listHasilTerapi.clear()
                listHasilTerapi.addAll(it.toObjects(HasilTerapi::class.java))
                if (listHasilTerapi.size <= 0) {
                    rv_hasil_terapi.visibility = View.INVISIBLE
                    state_empty.visibility = View.VISIBLE
                } else {
                    rv_hasil_terapi.visibility = View.VISIBLE
                    state_empty.visibility = View.GONE
                    adapter.setData(listHasilTerapi)
                }
            }
            .addOnFailureListener {
                hideLoading()
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun showLoading() {
        swipe_refresh.isRefreshing = true
    }

    private fun hideLoading() {
        swipe_refresh.isRefreshing = false
    }
}