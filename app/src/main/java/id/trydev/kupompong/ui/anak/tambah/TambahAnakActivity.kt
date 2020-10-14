package id.trydev.kupompong.ui.anak.tambah

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.setPadding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.trydev.kupompong.R
import id.trydev.kupompong.model.Anak
import kotlinx.android.synthetic.main.activity_anak.*
import kotlinx.android.synthetic.main.activity_anak.add_img_profil
import kotlinx.android.synthetic.main.activity_anak.btn_back
import kotlinx.android.synthetic.main.activity_anak.btn_submit
import kotlinx.android.synthetic.main.activity_anak.edt_alamat
import kotlinx.android.synthetic.main.activity_anak.edt_bahasa
import kotlinx.android.synthetic.main.activity_anak.edt_diagnosis
import kotlinx.android.synthetic.main.activity_anak.edt_gejala
import kotlinx.android.synthetic.main.activity_anak.edt_medical_treatment
import kotlinx.android.synthetic.main.activity_anak.edt_motorik
import kotlinx.android.synthetic.main.activity_anak.edt_nama
import kotlinx.android.synthetic.main.activity_anak.edt_pendidikan
import kotlinx.android.synthetic.main.activity_anak.edt_riwayat_terapi
import kotlinx.android.synthetic.main.activity_anak.edt_tempat_lahir
import kotlinx.android.synthetic.main.activity_anak.edt_tgl_lahir
import kotlinx.android.synthetic.main.activity_anak.edt_umur
import kotlinx.android.synthetic.main.activity_anak.img_profil
import kotlinx.android.synthetic.main.activity_anak.progress_bar
import kotlinx.android.synthetic.main.activity_anak.radioGroup
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class TambahAnakActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val rcStorage = 100
        const val rcCamera = 101
        const val rcChoose = 102
    }

    private var imageUri: Uri? = null
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mStorage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anak)

        supportActionBar?.hide()

        btn_back.setOnClickListener {
            finish()
        }

        add_img_profil.setOnClickListener {
            getStorage()
        }

        btn_submit.setOnClickListener {
            if (validate()) {
                showLoading()

                val dataPath = mFirestore.collection("anak")
                val key = dataPath.document().id

                val storagePath = "anak/${imageUri?.path?.split("/")?.last()}"
                val photoRef = mStorage.getReference(storagePath)
                photoRef.putFile(imageUri as Uri).
                        continueWithTask {task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            return@continueWithTask photoRef.downloadUrl
                        }
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val anak = Anak(
                                key,
                                edt_nama.text.toString(),
                                edt_tempat_lahir.text.toString(),
                                edt_tgl_lahir.text.toString(),
                                findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString(),
                                edt_umur.text.toString().toInt(),
                                edt_alamat.text.toString(),
                                edt_pendidikan.text.toString(),
                                edt_motorik.text.toString(),
                                edt_bahasa.text.toString(),
                                edt_diagnosis.text.toString(),
                                edt_gejala.text.toString(),
                                edt_riwayat_terapi.text.toString(),
                                edt_medical_treatment.text.toString(),
                                storagePath,
                                it.result.toString(),
                                Date(),
                                Date()
                            )
                            dataPath.document(key)
                                .set(anak)
                                .addOnSuccessListener {
                                    hideLoading()
                                    finish()
                                }
                                .addOnFailureListener {e ->
                                    hideLoading()
                                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Log.d("ON_COMPLETE_FAILED", it.exception?.localizedMessage.toString())
                            Toast.makeText(this, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }

    private fun showLoading() {
        progress_bar.visibility = View.VISIBLE
        btn_submit.visibility = View.INVISIBLE
        edt_nama.isEnabled = false
        edt_tempat_lahir.isEnabled = false
        edt_tgl_lahir.isEnabled = false
        radioGroup.isEnabled = false
        jk_lk.isEnabled = false
        jk_pr.isEnabled = false
        edt_umur.isEnabled = false
        edt_alamat.isEnabled = false
        edt_pendidikan.isEnabled = false
        edt_motorik.isEnabled = false
        edt_bahasa.isEnabled = false
        edt_diagnosis.isEnabled = false
        edt_gejala.isEnabled = false
        edt_riwayat_terapi.isEnabled = false
        edt_medical_treatment.isEnabled = false
    }

    private fun hideLoading() {
        progress_bar.visibility = View.GONE
        btn_submit.visibility = View.VISIBLE
        edt_nama.isEnabled = true
        edt_tempat_lahir.isEnabled = true
        edt_tgl_lahir.isEnabled = true
        radioGroup.isEnabled = true
        jk_lk.isEnabled = false
        jk_pr.isEnabled = false
        edt_umur.isEnabled = true
        edt_alamat.isEnabled = true
        edt_pendidikan.isEnabled = true
        edt_motorik.isEnabled = true
        edt_bahasa.isEnabled = true
        edt_diagnosis.isEnabled = true
        edt_gejala.isEnabled = true
        edt_riwayat_terapi.isEnabled = true
        edt_medical_treatment.isEnabled = true
    }

    private fun validate(): Boolean {
        if (edt_nama.text.isEmpty()) {
            edt_nama.requestFocus()
            edt_nama.error = "Wajib diisi"
            return false
        }
        if (edt_tempat_lahir.text.isEmpty()) {
            edt_tempat_lahir.requestFocus()
            edt_tempat_lahir.error = "Wajib diisi"
            return false
        }

        if (edt_tgl_lahir.text.isEmpty()) {
            edt_tgl_lahir.requestFocus()
            edt_tgl_lahir.error = "Wajib diisi"
            return false
        }

        if (radioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Mohon pilih jenis kelamin terlebih dahulu!", Toast.LENGTH_LONG).show()
            return false
        }

        if (edt_umur.text.isEmpty()) {
            edt_umur.requestFocus()
            edt_umur.error = "Wajib diisi"
            return false
        }

        if (edt_alamat.text.isEmpty()) {
            edt_alamat.requestFocus()
            edt_alamat.error = "Wajib diisi"
            return false
        }
        if (edt_pendidikan.text.isEmpty()) {
            edt_pendidikan.requestFocus()
            edt_pendidikan.error = "Wajib diisi"
            return false
        }
        if (edt_motorik.text.isEmpty()) {
            edt_motorik.requestFocus()
            edt_motorik.error = "Wajib diisi"
            return false
        }
        if (edt_bahasa.text.isEmpty()) {
            edt_bahasa.requestFocus()
            edt_bahasa.error = "Wajib diisi"
            return false
        }
        if (edt_diagnosis.text.isEmpty()) {
            edt_diagnosis.requestFocus()
            edt_diagnosis.error = "Wajib diisi"
            return false
        }
        if (edt_gejala.text.isEmpty()) {
            edt_gejala.requestFocus()
            edt_gejala.error = "Wajib diisi"
            return false
        }
        if (edt_riwayat_terapi.text.isEmpty()) {
            edt_riwayat_terapi.requestFocus()
            edt_riwayat_terapi.error = "Wajib diisi"
            return false
        }
        if (edt_medical_treatment.text.isEmpty()) {
            edt_medical_treatment.requestFocus()
            edt_medical_treatment.error = "Wajib diisi"
            return false
        }
        if (imageUri == null) {
            Toast.makeText(this, "Pilih foto anak terlebih dahulu", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    @AfterPermissionGranted(rcStorage)
    private fun getStorage() {
        if (hasPermissionStorage()) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Pilih Foto Pofil"), rcChoose)
        } else {
            EasyPermissions.requestPermissions(
                this,
                resources.getString(R.string.rationale_ask),
                rcStorage,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun hasPermissionCamera(): Boolean = EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    private fun hasPermissionStorage(): Boolean = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcChoose && resultCode == Activity.RESULT_OK && data!=null) {
            img_profil.setImageURI(data.data)
            img_profil.setPadding(0)
            this.imageUri = data.data
        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            Log.d("APP_SETTING_DIALOG", "Granted permission")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("PERMISSION_GRANTED", "GRANTED")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
    }


}