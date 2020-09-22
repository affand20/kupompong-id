package id.trydev.kupompong.ui.anak

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.DaftarAnakAdapter
import id.trydev.kupompong.model.Anak
import id.trydev.kupompong.ui.anak.tambah.TambahAnakActivity
import kotlinx.android.synthetic.main.fragment_anak.*

class AnakFragment : Fragment() {

    private lateinit var anakViewModel: AnakViewModel
    private lateinit var adapter: DaftarAnakAdapter

    private val mFirestore = FirebaseFirestore.getInstance()

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
                Intent(requireActivity(), TambahAnakActivity::class.java)
                    .putExtra("data_anak", it)
            )
        }

        rv_anak.layoutManager = LinearLayoutManager(requireContext())
        rv_anak.adapter = adapter

        fab_add.setOnClickListener {
            startActivity(Intent(requireActivity(), TambahAnakActivity::class.java))
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
            .get()
            .addOnSuccessListener {
                hideLoading()
                val listData = mutableListOf<Anak>()
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