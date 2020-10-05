package id.trydev.kupompong.ui.progress.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.R
import id.trydev.kupompong.model.HasilTerapi
import id.trydev.kupompong.utils.GlideApp
import id.trydev.kupompong.utils.Mode
import kotlinx.android.synthetic.main.activity_detail_progress.btn_delete
import kotlinx.android.synthetic.main.activity_detail_progress.btn_back
import kotlinx.android.synthetic.main.activity_detail_progress.btn_submit
import kotlinx.android.synthetic.main.activity_detail_progress.btn_toggle_mode
import kotlinx.android.synthetic.main.activity_detail_progress.edt_respon_anak
import kotlinx.android.synthetic.main.activity_detail_progress.edt_tindak_lanjut
import kotlinx.android.synthetic.main.activity_detail_progress.edt_topik
import kotlinx.android.synthetic.main.activity_detail_progress.img_profil
import kotlinx.android.synthetic.main.activity_detail_progress.progress_bar
import kotlinx.android.synthetic.main.activity_detail_progress.spinner_mastery
import kotlinx.android.synthetic.main.activity_detail_progress.spinner_prompt
import kotlinx.android.synthetic.main.activity_detail_progress.tv_nama_pasien

class DetailProgressActivity : AppCompatActivity() {

    private var hasilTerapi: HasilTerapi? = null
    private var mode = Mode.READ

    private val mFirestore = FirebaseFirestore.getInstance()
    private lateinit var listPromptLevel: Array<String>
    private lateinit var listMasteryLevel: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_progress)

        supportActionBar?.hide()

        listPromptLevel = resources.getStringArray(R.array.level_of_prompt)
        listMasteryLevel = resources.getStringArray(R.array.level_of_mastery)

        hasilTerapi = intent?.getSerializableExtra("hasil_terapi") as HasilTerapi

        img_profil.setPadding(0)
        GlideApp.with(this)
            .asBitmap()
            .load(hasilTerapi?.photoUrl)
            .into(img_profil)

        setItem(hasilTerapi)
        disableInput()



        btn_toggle_mode.setOnClickListener {
            if (mode == Mode.READ) {
                mode = Mode.EDIT
                enableInput()
                btn_toggle_mode.text = "Batalkan"
            } else {
                val alertDialog = AlertDialog.Builder(this, R.style.DefaultAlertDialog)
                alertDialog.setMessage("Anda yakin mau membatalkan perubahan saat ini?")
                alertDialog.setPositiveButton("Ya") { dialog, id ->
                    btn_toggle_mode.text = "Ubah"
                    mode = Mode.READ
                    setItem(hasilTerapi)
                    disableInput()
                    dialog.dismiss()
                }
                alertDialog.setNegativeButton("Tidak") { dialog, id ->
                    dialog.dismiss()
                }

                alertDialog.show()
            }
        }

        btn_submit.setOnClickListener {
            if (mode != Mode.READ) {
                if (validate()) {
                    showLoading()
                    val mapUpdate = hashMapOf<String, Any>()

                    if (edt_topik.text.toString() != hasilTerapi?.topik) {
                        mapUpdate["topik"] = edt_topik.text.toString()
                    }
                    if (spinner_prompt.selectedItem != hasilTerapi?.levelOfPrompt) {
                        mapUpdate["levelOfPrompt"] = spinner_prompt.selectedItem
                    }
                    if (spinner_mastery.selectedItem != hasilTerapi?.levelOfMastery) {
                        mapUpdate["levelOfMastery"] = spinner_mastery.selectedItem
                    }
                    if (edt_respon_anak.text.toString() != hasilTerapi?.responAnak) {
                        mapUpdate["responAnak"] = edt_respon_anak.text.toString()
                    }
                    if (edt_tindak_lanjut.text.toString() != hasilTerapi?.tindakLanjut) {
                        mapUpdate["tindakLanjut"] = edt_tindak_lanjut.text.toString()
                    }

                    if (mapUpdate.size <= 0) {
                        disableInput()
                        setItem(hasilTerapi)
                        hideLoading()
                    } else {
                        mFirestore.collection("hasil_terapi")
                            .document(hasilTerapi?.id.toString())
                            .update(mapUpdate)
                            .addOnSuccessListener {
                                hideLoading()
                                mode = Mode.READ
                                btn_toggle_mode.text = "Ubah"
                                loadUpdatedData()
                                disableInput()
                            }
                            .addOnFailureListener {
                                hideLoading()
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                    }
                }
            }
        }

        btn_delete.setOnClickListener {
            deleteItem(hasilTerapi)
        }

        btn_back.setOnClickListener {
            finish()
        }

    }

    private fun loadUpdatedData() {
        mFirestore.collection("hasil_terapi")
            .document(hasilTerapi?.id.toString())
            .get()
            .addOnSuccessListener {
                val terapi = it.toObject(HasilTerapi::class.java)
                if (terapi != null) {
                    this.hasilTerapi = terapi
                }
                setItem(hasilTerapi)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteItem(data: HasilTerapi?) {
        showLoading()
        mFirestore.collection("hasil_terapi")
            .document(data?.id.toString())
            .delete()
            .addOnSuccessListener {
                hideLoading()
                Toast.makeText(this, "Berhasil menghapus data", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {
                hideLoading()
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun setItem(data: HasilTerapi?) {
        Log.d("PROMPT", "${listPromptLevel.indexOf(data?.levelOfPrompt)}")
        tv_nama_pasien.text = data?.namaAnak
        edt_topik.setText(data?.topik)
        spinner_prompt.setSelection(listPromptLevel.indexOf(data?.levelOfPrompt))
        spinner_mastery.setSelection(listMasteryLevel.indexOf(data?.levelOfMastery))
        edt_respon_anak.setText(data?.responAnak)
        edt_tindak_lanjut.setText(data?.tindakLanjut)
    }

    private fun validate(): Boolean {
        if (edt_topik.text.isEmpty()) {
            edt_topik.error = "Wajib diisi!"
            edt_topik.requestFocus()
            return false
        }

        if (edt_respon_anak.text.isEmpty()) {
            edt_respon_anak.error = "Wajib diisi!"
            edt_respon_anak.requestFocus()
            return false
        }

        if (edt_tindak_lanjut.text.isEmpty()) {
            edt_tindak_lanjut.error = "Wajib diisi!"
            edt_tindak_lanjut.requestFocus()
            return false
        }
        return true
    }

    private fun disableInput() {
        edt_topik.isEnabled = false
        spinner_mastery.isEnabled = false
        spinner_prompt.isEnabled = false
        edt_respon_anak.isEnabled = false
        edt_tindak_lanjut.isEnabled = false
        btn_delete.visibility = View.VISIBLE
        btn_submit.visibility = View.INVISIBLE
    }

    private fun enableInput() {
        edt_topik.isEnabled = true
        spinner_mastery.isEnabled = true
        spinner_prompt.isEnabled = true
        edt_respon_anak.isEnabled = true
        edt_tindak_lanjut.isEnabled = true
        btn_delete.visibility = View.INVISIBLE
        btn_submit.visibility = View.VISIBLE
    }

    private fun showLoading() {
        if (mode == Mode.READ) {
            btn_delete.visibility = View.INVISIBLE
        } else {
            btn_submit.visibility = View.INVISIBLE
        }
        disableInput()
        progress_bar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        if (mode == Mode.READ) {
            btn_delete.visibility = View.VISIBLE
        } else {
            btn_submit.visibility = View.VISIBLE
        }
        enableInput()
        progress_bar.visibility = View.GONE
    }
}