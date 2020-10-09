package id.trydev.kupompong.ui.anak.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.ProgressTerapiAdapter
import id.trydev.kupompong.model.HasilTerapi
import id.trydev.kupompong.ui.progress.detail.DetailProgressActivity
import kotlinx.android.synthetic.main.activity_progress_terapi.*

class DataTerapiActivity : AppCompatActivity() {

    private val mFirestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: ProgressTerapiAdapter

    private lateinit var idAnak: String

//    override fun onResume() {
//        super.onResume()
//        getData(idAnak)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_terapi)

        idAnak = intent?.getStringExtra("idAnak").toString()
        Log.d("ID-ANAK", idAnak)

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
            getData(idAnak)
        }

        getData(idAnak)

    }

    private fun getData(idAnak: String) {
        showLoading()
        mFirestore.collection("hasil_terapi")
//            .whereEqualTo("idAnak", idAnak)
            .orderBy("dateTerapi")
            .get()
            .addOnSuccessListener {
                hideLoading()
                var listHasilTerapi = mutableListOf<HasilTerapi>()
                listHasilTerapi.addAll(it.toObjects(HasilTerapi::class.java))
                listHasilTerapi = listHasilTerapi.filter {
                    it.idAnak == idAnak
                }.toMutableList()
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