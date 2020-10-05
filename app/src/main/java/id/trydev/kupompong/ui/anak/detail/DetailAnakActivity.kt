package id.trydev.kupompong.ui.anak.detail

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
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.setPadding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.trydev.kupompong.R
import id.trydev.kupompong.model.Anak
import id.trydev.kupompong.ui.anak.tambah.TambahAnakActivity
import id.trydev.kupompong.utils.GlideApp
import id.trydev.kupompong.utils.Mode
import kotlinx.android.synthetic.main.activity_detail_anak.*
import kotlinx.android.synthetic.main.activity_detail_anak.add_img_profil
import kotlinx.android.synthetic.main.activity_detail_anak.btn_back
import kotlinx.android.synthetic.main.activity_detail_anak.btn_submit
import kotlinx.android.synthetic.main.activity_detail_anak.edt_alamat
import kotlinx.android.synthetic.main.activity_detail_anak.edt_bahasa
import kotlinx.android.synthetic.main.activity_detail_anak.edt_diagnosis
import kotlinx.android.synthetic.main.activity_detail_anak.edt_gejala
import kotlinx.android.synthetic.main.activity_detail_anak.edt_medical_treatment
import kotlinx.android.synthetic.main.activity_detail_anak.edt_motorik
import kotlinx.android.synthetic.main.activity_detail_anak.edt_nama
import kotlinx.android.synthetic.main.activity_detail_anak.edt_pendidikan
import kotlinx.android.synthetic.main.activity_detail_anak.edt_riwayat_terapi
import kotlinx.android.synthetic.main.activity_detail_anak.edt_tempat_lahir
import kotlinx.android.synthetic.main.activity_detail_anak.edt_tgl_lahir
import kotlinx.android.synthetic.main.activity_detail_anak.img_profil
import kotlinx.android.synthetic.main.activity_detail_anak.progress_bar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class DetailAnakActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val rcStorage = 100
        const val rcCamera = 101
        const val rcChoose = 102
    }

    private var imageUri: Uri? = null

    private var mode = Mode.READ
    private val mStorage = FirebaseStorage.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()

    private lateinit var data: Anak

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_anak)

        supportActionBar?.hide()
        val intentExtra = intent?.getSerializableExtra("data_anak") as Anak
        this.data = intentExtra

        setItem(data)
        disableInput()

        btn_back.setOnClickListener {
            finish()
        }

        add_img_profil.setOnClickListener {
            getStorage()
        }

        btn_toggle_mode.setOnClickListener {
            if (mode == Mode.READ) {
                btn_toggle_mode.text = "Batalkan"
                mode = Mode.EDIT
                enableInput()
                btn_delete.visibility = View.GONE
                btn_submit.visibility = View.VISIBLE
            } else {

                val alertDialog = AlertDialog.Builder(this, R.style.DefaultAlertDialog)
                alertDialog.setMessage("Anda yakin mau membatalkan perubahan saat ini?")
                alertDialog.setPositiveButton("Ya") { dialog, id ->
                    btn_toggle_mode.text = "Ubah"
                    mode = Mode.READ
                    setItem(data)
                    disableInput()
                    dialog.dismiss()
                }
                alertDialog.setNegativeButton("Tidak") { dialog, id ->
                    dialog.dismiss()
                }

                alertDialog.show()
            }
        }

        btn_delete.setOnClickListener {
            deleteItem(data)
        }

        btn_submit.setOnClickListener {
            if (mode != Mode.READ) {
                if (validate()) {
                    showLoading()
                    val mapUpdate = hashMapOf<String, Any>()

                    if (edt_nama.text.toString()!=data.fullName) {
                        mapUpdate["fullName"] = edt_nama.text.toString()
                    }
                    if (edt_tempat_lahir.text.toString() != data.birthPlace) {
                        mapUpdate["birthPlace"] = edt_tempat_lahir.text.toString()
                    }
                    if (edt_tgl_lahir.text.toString() != data.dateOfBirth) {
                        mapUpdate["dateOfBirth"] = edt_tgl_lahir.text.toString()
                    }
                    if (findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text != data.gender) {
                        mapUpdate["gender"] = findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString()
                    }
                    if (edt_umur.text.toString() != data.old.toString()) {
                        mapUpdate["old"] = edt_umur.text.toString().toInt()
                    }
                    if (edt_alamat.text.toString() != data.address) {
                        mapUpdate["address"] = edt_alamat.text.toString()
                    }
                    if (edt_pendidikan.text.toString() != data.education) {
                        mapUpdate["education"] = edt_pendidikan.text.toString()
                    }
                    if (edt_motorik.text.toString() != data.motorik) {
                        mapUpdate["motorik"] = edt_motorik.text.toString()
                    }
                    if (edt_bahasa.text.toString() != data.language) {
                        mapUpdate["language"] = edt_bahasa.text.toString()
                    }
                    if (edt_diagnosis.text.toString() != data.diagnose) {
                        mapUpdate["diagnose"] = edt_diagnosis.text.toString()
                    }
                    if (edt_gejala.text.toString() != data.gejala) {
                        mapUpdate["gejala"] = edt_gejala.text.toString()
                    }
                    if (edt_riwayat_terapi.text.toString() != data.history) {
                        mapUpdate["history"] = edt_riwayat_terapi.text.toString()
                    }
                    if (edt_medical_treatment.text.toString() != data.medicalTreatment) {
                        mapUpdate["medicalTreatment"] = edt_medical_treatment.text.toString()
                    }

                    Log.d("SUBMIT", "$mapUpdate")
                    Log.d("SUBMIT", "${mapUpdate.size}")

                    if (mapUpdate.size < 1 && imageUri == null) {
                        btn_toggle_mode.text = "Ubah"
                        mode = Mode.READ
                        setItem(data)
                        hideLoading()

                        Log.d("NO_UPDATE", "$mode")
                        disableInput()
                    } else {

                        if (imageUri != null) {

                            val path = "${data.imgPath?.split("/")?.dropLast(1)}/"
                            val ref = mStorage.getReference("$path${imageUri?.path?.split('/')?.last()}")

                            ref.putFile(imageUri.toString().toUri())
                                .continueWithTask {task ->
                                    if (!task.isSuccessful) {
                                        task.exception?.let {
                                            throw it
                                        }
                                    }
                                    return@continueWithTask ref.downloadUrl
                                }
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val newUrl = it.result.toString()

                                        mapUpdate["photoUrl"] = newUrl
                                        mapUpdate["imgPath"] = "$path${imageUri?.path?.split('/')?.last()}"

                                        mFirestore.collection("anak")
                                            .document(data.id.toString())
                                            .update(mapUpdate)
                                            .addOnSuccessListener {
                                                Toast.makeText(this, "Berhasil memperbarui data!", Toast.LENGTH_LONG).show()

//                                finish()

                                                btn_toggle_mode.text = "Ubah"
                                                mode = Mode.READ
                                                loadUpdatedData()
                                                hideLoading()

                                                disableInput()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                                                hideLoading()
                                            }

                                    } else {
                                        Toast.makeText(this, it.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            mFirestore.collection("anak")
                                .document(data.id.toString())
                                .update(mapUpdate)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Berhasil memperbarui data!", Toast.LENGTH_LONG).show()

    //                                finish()
                                    mode = Mode.READ
                                    btn_toggle_mode.text = "Ubah"
                                    loadUpdatedData()
                                    hideLoading()

                                    disableInput()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                                    hideLoading()
                                }
                        }

                    }

                }

            }
        }

    }

    private fun loadUpdatedData() {
//        showLoading()
        mFirestore.collection("anak")
            .document(data.id.toString())
            .get()
            .addOnSuccessListener {
                val anak = it.toObject(Anak::class.java)
                if (anak != null) {
                    this.data = anak
                }
                setItem(data)
//                hideLoading()
            }
            .addOnFailureListener {
//                hideLoading()
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun setItem(item: Anak) {
        tv_title.text = item.fullName
        edt_nama.setText(item.fullName)
        edt_tempat_lahir.setText(item.birthPlace)
        edt_tgl_lahir.setText(item.dateOfBirth)
        if (item.gender == "Laki-laki") {
            jk_lk.isChecked = true
        } else {
            jk_pr.isChecked = true
        }
        edt_umur.setText(item.old.toString())
        edt_alamat.setText(item.address)
        edt_pendidikan.setText(item.education)
        edt_motorik.setText(item.motorik)
        edt_bahasa.setText(item.language)
        edt_diagnosis.setText(item.diagnose)
        edt_gejala.setText(item.gejala)
        edt_riwayat_terapi.setText(item.history)
        edt_medical_treatment.setText(item.medicalTreatment)

        GlideApp.with(this)
            .asBitmap()
            .load(item.photoUrl)
            .centerCrop()
            .into(img_profil)
        img_profil.setPadding(0)
    }

    private fun deleteItem(item: Anak) {
        showLoading()
        mFirestore.collection("anak")
            .document(item.id.toString())
            .delete()
            .addOnSuccessListener {
                hideLoading()
                mStorage.getReference(item.imgPath.toString()).delete()
                    .addOnSuccessListener {
                        hideLoading()
                        Toast.makeText(this, "Berhasil menghapus data!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        hideLoading()
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                hideLoading()
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun disableInput() {
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
        add_img_profil.visibility = View.GONE

        btn_delete.visibility = View.VISIBLE
        btn_submit.visibility = View.GONE
    }

    private fun enableInput() {
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
        add_img_profil.visibility = View.VISIBLE

        btn_delete.visibility = View.GONE
        btn_submit.visibility = View.VISIBLE
    }

    private fun showLoading() {
        if (mode == Mode.READ) {
            btn_delete.visibility = View.INVISIBLE
        } else {
            btn_submit.visibility = View.INVISIBLE
        }
        progress_bar.visibility = View.VISIBLE
        edt_nama.isEnabled = false
        edt_tempat_lahir.isEnabled = false
        edt_tgl_lahir.isEnabled = false
        radioGroup.isEnabled = false
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
        if (mode == Mode.READ) {
            btn_delete.visibility = View.VISIBLE
        } else {
            btn_submit.visibility = View.VISIBLE
        }
        progress_bar.visibility = View.GONE
        edt_nama.isEnabled = true
        edt_tempat_lahir.isEnabled = true
        edt_tgl_lahir.isEnabled = true
        radioGroup.isEnabled = true
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
        // User has input new photo
        if (mode == Mode.EDIT_FILLED) {
            if (imageUri == null) {
                Toast.makeText(this, "Pilih foto anak terlebih dahulu", Toast.LENGTH_LONG).show()
                return false
            }
        }

        return true
    }

    @AfterPermissionGranted(rcStorage)
    private fun getStorage() {
        if (hasPermissionStorage()) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Pilih Foto Pofil"),
                rcChoose
            )
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
            this.mode = Mode.EDIT_FILLED
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