package id.trydev.kupompong.ui.progress.tambah

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.setPadding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.R
import id.trydev.kupompong.model.Anak
import id.trydev.kupompong.model.HasilTerapi
import id.trydev.kupompong.utils.GlideApp
import kotlinx.android.synthetic.main.activity_input_progress_terapi.*
import kotlinx.android.synthetic.main.activity_input_progress_terapi.btn_back
import kotlinx.android.synthetic.main.activity_input_progress_terapi.progress_bar

class InputProgressTerapiActivity : AppCompatActivity() {

    private val mFirestore = FirebaseFirestore.getInstance()

    private var anak: Anak? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_progress_terapi)

        this.anak = intent.getSerializableExtra("anak") as Anak

        supportActionBar?.hide()

        img_profil.setPadding(0)
        GlideApp.with(this)
            .asBitmap()
            .load(anak?.photoUrl)
            .into(img_profil)

        tv_nama_pasien.text = anak?.fullName

        btn_back.setOnClickListener {
            finish()
        }

        btn_submit.setOnClickListener {
            if (validate()) {
                showLoading()

                val ref = mFirestore.collection("hasil_terapi")
                val id = ref.document().id

                val progress = HasilTerapi(
                    id,
                    anak?.id,
                    anak?.fullName,
                    edt_topik.text.toString(),
                    spinner_prompt.selectedItem.toString(),
                    spinner_mastery.selectedItem.toString(),
                    edt_respon_anak.text.toString(),
                    edt_tindak_lanjut.text.toString(),
                    Timestamp.now()
                )

                ref.document(id)
                    .set(progress)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Berhasil menambahkan progress terapi!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }

            }
        }

    }

    private fun showLoading() {
        progress_bar.visibility = View.VISIBLE
        btn_submit.visibility = View.INVISIBLE
        edt_topik.isEnabled = false
        spinner_mastery.isEnabled = false
        spinner_prompt.isEnabled = false
        edt_respon_anak.isEnabled = false
        edt_tindak_lanjut.isEnabled = false
    }

    private fun hideLoading() {
        progress_bar.visibility = View.GONE
        btn_submit.visibility = View.VISIBLE
        edt_topik.isEnabled = true
        spinner_mastery.isEnabled = true
        spinner_prompt.isEnabled = true
        edt_respon_anak.isEnabled = true
        edt_tindak_lanjut.isEnabled = true
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


}