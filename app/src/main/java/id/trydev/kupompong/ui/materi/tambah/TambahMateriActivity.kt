package id.trydev.kupompong.ui.materi.tambah

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.setPadding
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.trydev.kupompong.R
import id.trydev.kupompong.utils.GlideApp
import kotlinx.android.synthetic.main.activity_tambah_materi.*
import kotlinx.android.synthetic.main.activity_tambah_materi.btn_back
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class TambahMateriActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

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
        setContentView(R.layout.activity_tambah_materi)

        supportActionBar?.hide()

        btn_back.setOnClickListener {
            finish()
        }

        btn_save.setOnClickListener {
            // input to database
        }

        iv_input_img.setOnClickListener {
            getStorage()
        }

        btn_suara.setOnClickListener {

        }

        btn_play.setOnClickListener {

        }

        btn_simpan_pilihan.setOnClickListener {

        }
    }

    private fun validate(): Boolean {
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
//            iv_input_img.setImageURI(data.data)
            tv_state_upload_img.visibility = View.GONE
            iv_input_img.setPadding(0)
            GlideApp.with(this)
                .asBitmap()
                .load(data.data)
                .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_materi)))
                .into(iv_input_img)
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