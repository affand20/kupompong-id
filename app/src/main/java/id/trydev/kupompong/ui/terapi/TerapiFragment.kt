package id.trydev.kupompong.ui.terapi

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.DaftarAnakAdapterLite
import id.trydev.kupompong.adapter.FaseAdapter
import id.trydev.kupompong.adapter.MateriAdapterLite
import id.trydev.kupompong.model.Anak
import id.trydev.kupompong.model.Fase
import id.trydev.kupompong.model.Materi
import id.trydev.kupompong.ui.terapi.fase4.Fase4Activity
import id.trydev.kupompong.utils.GlideApp
import kotlinx.android.synthetic.main.fragment_terapi.*

class TerapiFragment : Fragment() {

    private lateinit var terapiViewModel: TerapiViewModel

    private val mFirestore = FirebaseFirestore.getInstance()

    private val listAnak = mutableListOf<Anak>()
    private val listMateri = mutableListOf<Materi>()
    private val listFase = mutableListOf<Fase>()

    private lateinit var adapterAnak: DaftarAnakAdapterLite
    private lateinit var adapterMateri: MateriAdapterLite
    private lateinit var adapterFase: FaseAdapter

    private lateinit var btnFilter: ImageButton
    private lateinit var searchBox: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var stateEmpty: TextView

    private lateinit var dialog: AlertDialog

    private lateinit var anak: Anak
    private lateinit var materi: Materi
    private lateinit var fase: Fase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terapi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

//            Log.d("PILIH FASE", "${this.fase.level} == 4 ? ${this.fase.level == 4}, ${this.fase.level is Int}")

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

        btn_start_terapi.setOnClickListener {
            if (validate()) {
//                Log.d("ANAK", "${this.anak}")
//                Log.d("MATERI", "${this.materi}")
//                Log.d("FASE", "${this.fase}")
//                Log.d("CLICK", "FASE ${this.fase.level} ${this.fase.level==4}")
                when(this.fase.fase) {
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
                            Intent(requireContext(), Fase4Activity::class.java)
                                .putExtra("anak", anak)
                                .putExtra("materi", materi)
                                .putExtra("fase", this.fase)
                        )
                    }

                    6 -> {
                        startActivity(
                            Intent(requireContext(), Fase4Activity::class.java)
                                .putExtra("anak", anak)
                                .putExtra("materi", materi)
                                .putExtra("fase", this.fase)
                        )
                    }

                }
            }
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

            btnFilter = dialogView.findViewById(R.id.btn_filter)
            searchBox = dialogView.findViewById(R.id.edt_search)
            recyclerView = dialogView.findViewById(R.id.rv_anak)
            progressBar = dialogView.findViewById(R.id.progress_bar)
            stateEmpty = dialogView.findViewById(R.id.state_empty)

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapterAnak
            builder.setView(dialogView)
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
                tv_anak.visibility = View.VISIBLE
                pilihan_anak.visibility = View.GONE
                getData(type)
            }
            else if (type == "materi") {
                tv_template_materi.visibility = View.VISIBLE
                pilihan_materi.visibility = View.GONE
                getData(type)
            }
            if (type == "fase") {
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