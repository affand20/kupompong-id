package id.trydev.kupompong.ui.terapi

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.MainActivity
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.DaftarAnakAdapterLite
import id.trydev.kupompong.adapter.FaseAdapter
import id.trydev.kupompong.adapter.MateriAdapterLite
import id.trydev.kupompong.model.Anak
import id.trydev.kupompong.model.Fase
import id.trydev.kupompong.model.Materi
import id.trydev.kupompong.prefs.SharedPrefs
import id.trydev.kupompong.ui.login.LoginActivity
import id.trydev.kupompong.ui.panduan.PanduanActivity
import id.trydev.kupompong.ui.progress.ProgressTerapiActivity
import id.trydev.kupompong.ui.tentang.TentangActivity
import id.trydev.kupompong.ui.terapi.fase.Fase4Activity
import id.trydev.kupompong.ui.terapi.fase.Fase56Activity
import id.trydev.kupompong.utils.GlideApp
import kotlinx.android.synthetic.main.fragment_terapi.*

class TerapiFragment : Fragment() {

    private val mFirestore = FirebaseFirestore.getInstance()

    private val listAnak = mutableListOf<Anak>()
    private val listMateri = mutableListOf<Materi>()
    private val listFase = mutableListOf<Fase>()

    private lateinit var adapterAnak: DaftarAnakAdapterLite
    private lateinit var adapterMateri: MateriAdapterLite
    private lateinit var adapterFase: FaseAdapter

    private lateinit var searchBox: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var stateEmpty: TextView

    private lateinit var dialog: AlertDialog

    private var anak: Anak? = null
    private var materi: Materi? = null
    private var fase: Fase? = null

    private lateinit var prefs: SharedPrefs
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terapi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPrefs(requireContext())

        adapterAnak = DaftarAnakAdapterLite(requireContext()) {
            this.anak = it
            dialog.dismiss()
            tv_anak.visibility = View.GONE
            pilihan_anak.visibility = View.VISIBLE

            GlideApp.with(requireContext())
                .asBitmap()
                .load(it.photoUrl)
                .transform(CenterCrop(), RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.rounded_image)))
                .into(img_anak)

            tv_name.text = it.fullName
        }

        adapterMateri = MateriAdapterLite(requireContext()) {
            this.materi = it
            dialog.dismiss()

            tv_template_materi.visibility = View.GONE
            pilihan_materi.visibility = View.VISIBLE

            tv_title.text = it.judul
        }

        adapterFase = FaseAdapter(requireContext()) {
            this.fase = it
            dialog.dismiss()

            tv_fase_level.visibility = View.GONE
            pilihan_fase_level.visibility = View.VISIBLE

            tv_fase_title.text = it.title
        }

        val fase = resources.getStringArray(R.array.pilihan_fase)
        fase.forEachIndexed { index, s ->
            val item = s.replace(" ","").split("-")
            this.listFase.add(Fase(s, item[0].last().toString().toInt(), item[1].last().toString().toInt()))
        }
        adapterFase.setData(listFase)

        cv_anak.setOnClickListener{
            triggerDialog("anak")
        }

        tv_anak.setOnClickListener {
            triggerDialog("anak")
        }

        if (pilihan_anak.visibility == View.VISIBLE) {
            triggerDialog("anak")
        }

        cv_template_materi.setOnClickListener{
            triggerDialog("materi")
        }

        tv_template_materi.setOnClickListener {
            triggerDialog("materi")
        }

        if (pilihan_materi.visibility == View.VISIBLE) {
            triggerDialog("materi")
        }

        cv_fase_level.setOnClickListener{
            triggerDialog("fase")
        }

        tv_fase_level.setOnClickListener {
            triggerDialog("fase")
        }

        if (pilihan_fase_level.visibility == View.VISIBLE) {
            triggerDialog("fase")
        }

        tv_data_terapi.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProgressTerapiActivity::class.java)
            )
        }

        cv_data_terapi.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProgressTerapiActivity::class.java)
            )
        }

        iv_data_terapi.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProgressTerapiActivity::class.java)
            )
        }


        btn_start_terapi.setOnClickListener {
            if (validate()) {
                when(this.fase?.fase) {
                    4 -> {
                        Log.d("MASUK", "MASUK WOI")
                        startActivity(
                            Intent(requireContext(), Fase4Activity::class.java)
                                .putExtra("anak", anak)
                                .putExtra("materi", materi)
                                .putExtra("fase", this.fase)
                        )
                    }

                    5 -> {
                        startActivity(
                            Intent(requireContext(), Fase56Activity::class.java)
                                .putExtra("anak", anak)
                                .putExtra("materi", materi)
                                .putExtra("fase", this.fase)
                        )
                    }

                    6 -> {
                        startActivity(
                            Intent(requireContext(), Fase56Activity::class.java)
                                .putExtra("anak", anak)
                                .putExtra("materi", materi)
                                .putExtra("fase", this.fase)
                        )
                    }

                }
            }
        }

        btn_logout.setOnClickListener {
            prefs.resetPrefs()
            mAuth.signOut()
            startActivity(
                Intent(
                    requireContext(), LoginActivity::class.java
                )
            )
            MainActivity().finish()
        }

        btn_about.setOnClickListener {
            startActivity(
                Intent(requireContext(), TentangActivity::class.java)
            )
        }

        btn_guide.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(), PanduanActivity::class.java
                )
            )
        }

    }

    private fun validate(): Boolean {
        if (this.anak == null) {
            Toast.makeText(requireContext(), "Anda belum memilih data Anak", Toast.LENGTH_LONG).show()
            return false
        }
        if (this.materi == null) {
            Toast.makeText(requireContext(), "Anda belum memilih data Materi", Toast.LENGTH_LONG).show()
            return false
        }
        if (this.fase == null) {
            Toast.makeText(requireContext(), "Anda belum memilih data Fase", Toast.LENGTH_LONG).show()
            return false
        }

        return true

    }

    private fun triggerDialog(type: String) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.RoundAlertDialog)

        if (type == "anak") {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pilih_anak, null)

            searchBox = dialogView.findViewById(R.id.edt_search)
            recyclerView = dialogView.findViewById(R.id.rv_anak)
            progressBar = dialogView.findViewById(R.id.progress_bar)
            stateEmpty = dialogView.findViewById(R.id.state_empty)

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapterAnak
            builder.setView(dialogView)

            searchBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val listFiltered = listAnak.filter {
                        it.fullName.toString().toLowerCase().contains(p0.toString())
                    }
                    adapterAnak.setData(listFiltered)
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
        }

        if (type == "materi") {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pilih_materi, null)

            searchBox = dialogView.findViewById(R.id.edt_search)
            recyclerView = dialogView.findViewById(R.id.rv_materi)
            progressBar = dialogView.findViewById(R.id.progress_bar)
            stateEmpty = dialogView.findViewById(R.id.state_empty)

            recyclerView.layoutManager = GridLayoutManager(requireContext(),3)
            recyclerView.adapter = adapterMateri
            builder.setView(dialogView)

            searchBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val listFiltered = listMateri.filter {
                        it.judul.toString().toLowerCase().contains(p0.toString())
                    }
                    adapterMateri.setData(listFiltered)
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
        }

        if (type == "fase") {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pilih_fase, null)

            recyclerView = dialogView.findViewById(R.id.rv_fase)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapterFase

            builder.setView(dialogView)
        }

        dialog = builder.create()
        dialog.setOnShowListener {
            if (type == "anak") {
                this.anak = null
                tv_anak.visibility = View.VISIBLE
                pilihan_anak.visibility = View.GONE
                getData(type)
            }
            else if (type == "materi") {
                this.materi = null
                tv_template_materi.visibility = View.VISIBLE
                pilihan_materi.visibility = View.GONE
                getData(type)
            }
            if (type == "fase") {
                this.fase = null
                tv_fase_level.visibility = View.VISIBLE
                pilihan_fase_level.visibility = View.GONE
            }
        }

        val scale = resources.displayMetrics.density
        dialog.window?.setLayout((.50 * scale).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)

        dialog.show()
    }

    private fun getData(type: String) {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
        mFirestore.collection(type)
            .get()
            .addOnSuccessListener {
                if (type == "anak") {
                    this.listAnak.clear()
                    this.listAnak.addAll(it.toObjects(Anak::class.java))
                    Log.d("LIST_ANAK", "${listAnak.size}")

                    if (listAnak.size <= 0) {
                        recyclerView.visibility = View.INVISIBLE
                        stateEmpty.visibility = View.VISIBLE
                    } else {
                        if (this::adapterAnak.isInitialized) {
                            adapterAnak.setData(listAnak)
                        }
                        recyclerView.visibility = View.VISIBLE
                        stateEmpty.visibility = View.INVISIBLE
                    }
                }

                if (type == "materi") {
                    this.listMateri.clear()
                    this.listMateri.addAll(it.toObjects(Materi::class.java))
                    Log.d("LIST_ANAK", "${listMateri.size}")

                    if (listMateri.size <= 0) {
                        recyclerView.visibility = View.INVISIBLE
                        stateEmpty.visibility = View.VISIBLE
                    } else {
                        if (this::adapterAnak.isInitialized) {
                            adapterMateri.setData(listMateri)
                        }
                        recyclerView.visibility = View.VISIBLE
                        stateEmpty.visibility = View.INVISIBLE
                    }
                }

                progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
}