package id.trydev.kupompong.ui.progress

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.ProgressTerapiAdapter
import id.trydev.kupompong.model.HasilTerapi
import id.trydev.kupompong.ui.progress.detail.DetailProgressActivity
import kotlinx.android.synthetic.main.activity_progress_terapi.*

class ProgressTerapiActivity : AppCompatActivity() {

    private val mFirestore = FirebaseFirestore.getInstance()

    private lateinit var adapter: ProgressTerapiAdapter

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

    }

    private fun getData() {
        showLoading()
        mFirestore.collection("hasil_terapi")
            .orderBy("dateTerapi")
            .get()
            .addOnSuccessListener {
                hideLoading()
                val listHasilTerapi = it.toObjects(HasilTerapi::class.java)
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