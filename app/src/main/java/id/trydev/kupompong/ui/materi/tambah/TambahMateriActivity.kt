package id.trydev.kupompong.ui.materi.tambah

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.PilihanAdapter
import id.trydev.kupompong.model.PilihanMateri
import id.trydev.kupompong.utils.GlideApp
import id.trydev.kupompong.utils.Mode
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
        const val rcAudio = 103
    }

    private var imageUri: Uri? = null
    private var audioUri: Uri? = null
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mStorage = FirebaseStorage.getInstance()

    private lateinit var player: MediaPlayer
    private lateinit var parentTitle: String
    private lateinit var parentKey: String

    private lateinit var adapter: PilihanAdapter

    private var mode = Mode.CREATE
    private var pilihan: PilihanMateri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_materi)

        supportActionBar?.hide()

        parentTitle = intent?.getStringExtra("judul").toString()
        parentKey = intent?.getStringExtra("materiId").toString()

        player = MediaPlayer()
        player.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        adapter = PilihanAdapter(this) { item, view ->
            val popup = PopupMenu(this, view)
            popup.inflate(R.menu.context_menu)
            popup.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.item_edit -> {
                        if (imageUri != null || audioUri!= null || edt_option_text.text.isNotEmpty() || pilihan != null) {
                            val alertDialog = AlertDialog.Builder(this, R.style.DefaultAlertDialog)
                            alertDialog.setMessage("Anda yakin mau membatalkan perubahan saat ini?")
                            alertDialog.setPositiveButton("Ya, Edit Sekarang") { dialog, id ->
                                setItem(item)
                                dialog.dismiss()
                            }
                            alertDialog.setNegativeButton("Tidak") { dialog, id ->
                                dialog.dismiss()
                            }

                            alertDialog.show()
                        } else {
                            setItem(item)
                        }
                        true
                    }

                    R.id.item_delete -> {
                        val alertDialog = AlertDialog.Builder(this, R.style.DefaultAlertDialog)
                        alertDialog.setMessage("Anda yakin mau menghapus pilihan ini?")
                        alertDialog.setPositiveButton("Ya") { dialog, _ ->
                            deleteOpt(item)
                            dialog.dismiss()
                        }
                        alertDialog.setNegativeButton("Tidak") { dialog, _ ->
                            dialog.dismiss()
                        }

                        alertDialog.show()
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }

        rv_item_jawaban.layoutManager = GridLayoutManager(this, 2)
        rv_item_jawaban.adapter = adapter

        getData()

        btn_back.setOnClickListener {
            if (player.isPlaying) {
                player.stop()
            }
            finish()
        }

        btn_save.setOnClickListener {
            if (player.isPlaying) {
                player.stop()
            }
            finish()
        }

        iv_input_img.setOnClickListener {
            if (player.isPlaying) {
                player.stop()
            }
            getStorage()
        }

        btn_suara.setOnClickListener {
            if (player.isPlaying) {
                player.stop()
            }
            getAudio()
        }

        btn_play.setOnClickListener {
            if (mode == Mode.CREATE) {
                if (audioUri != null) {
                    if (!player.isPlaying) {
                        player.reset()
                        player.setDataSource(this, audioUri.toString().toUri())

                        player.setOnPreparedListener {
                            player.start()
                        }
                        player.prepare()
                    } else {
                        player.stop()
                    }
                } else {
                    Toast.makeText(this, "Anda belum memilih file audio", Toast.LENGTH_LONG).show()
                }
            } else {
                if (!player.isPlaying) {
                    player.reset()

                    player.setOnPreparedListener {
                        player.start()
                    }

                    if (audioUri == null) {
                        player.setDataSource(pilihan?.audioUrl)
                        player.prepareAsync()
                    } else {
                        player.setDataSource(this, audioUri.toString().toUri())
                        player.prepare()
                    }

                } else {
                    player.stop()
                }
            }
        }

        btn_simpan_pilihan.setOnClickListener {
            Log.d("Mode", "$mode")
            if (player.isPlaying) {
                player.stop()
            }
            if (mode == Mode.CREATE) {
                if (validate()) {
                    showLoading("submit_opt")
                    uploadImageAudio(imageUri.toString().toUri(), audioUri.toString().toUri())
                }
            } else {
                Log.d("URI", "$imageUri, $audioUri")
                if (imageUri != null) {
                    deleteItem(pilihan?.imgPath.toString())
                    updateImage(pilihan, imageUri.toString().toUri())
                }
                if (audioUri != null) {
                    deleteItem(pilihan?.audioPath.toString())
                    updateAudio(pilihan, audioUri.toString().toUri())
                }
                if (pilihan?.caption != edt_option_text.text.toString()) {
                    // TODO : UPDATE CAPTION PILIHAN
                    updateCaption(pilihan?.id.toString(), edt_option_text.text.toString())
                }
            }
        }
    }

    private fun deleteOpt(item: PilihanMateri) {
        showLoading("load_opt")

        mFirestore.collection("materi")
            .document(parentKey)
            .collection("pilihan")
            .document(item.id.toString())
            .delete()
            .addOnSuccessListener {
                deleteItem(item.audioPath.toString())
                deleteItem(item.imgPath.toString())
                getData()
            }
            .addOnFailureListener {
                hideLoading("load_opt")
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }

    }

    private fun updateCaption(id: String, newCapt: String) {
        showLoading("submit_opt")
        mFirestore.collection("materi")
            .document(parentKey)
            .collection("pilihan")
            .document(id)
            .update("caption", newCapt)
            .addOnSuccessListener {
                hideLoading("submit_opt")
                clearInput()
                getData()
            }
            .addOnFailureListener {e->
                hideLoading("submit_opt")
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteItem(path: String) {
        mStorage.getReference(path).delete()
            .addOnSuccessListener {
                Log.d("ON_SUCCESS", "ITEM DELETED")
            }
            .addOnFailureListener {
                Log.d("ON_FAILURE", "ITEM FAILED DELETED")
            }
    }

    private fun updateAudio(item: PilihanMateri?, uri: Uri) {
        showLoading("submit_opt")
        val path = item?.audioPath.toString()
        val newPath = "${path.split("/").dropLast(1).joinToString("/")}/"
        Log.d("NEW_PATH", newPath)
        val ref = mStorage.getReference("$newPath${audioUri?.path?.split('/')?.last()}")
        ref.putFile(uri)
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

                    mFirestore.collection("materi")
                        .document(parentKey)
                        .collection("pilihan")
                        .document(item?.id.toString())
                        .update(hashMapOf<String, Any>(
                            "audioUrl" to newUrl,
                            "audioPath" to "$newPath${audioUri?.path?.split('/')?.last()}"
                        ))
                        .addOnSuccessListener {
                            hideLoading("submit_opt")
                            clearInput()
                            getData()
                        }
                        .addOnFailureListener {e->
                            hideLoading("submit_opt")
                            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                } else {
                    hideLoading("submit_opt")
                    Toast.makeText(this, it.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateImage(item: PilihanMateri?, uri: Uri) {
        showLoading("submit_opt")
        val path = item?.imgPath.toString()
        val newPath = "${path.split("/").dropLast(1).joinToString("/")}/"
        Log.d("NEW_PATH", newPath)
        val ref = mStorage.getReference("$newPath${imageUri?.path?.split('/')?.last()}")
        ref.putFile(uri)
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

                    mFirestore.collection("materi")
                        .document(parentKey)
                        .collection("pilihan")
                        .document(item?.id.toString())
                        .update(hashMapOf<String, Any>(
                            "imgUrl" to newUrl,
                            "imgPath" to "$newPath${imageUri?.path?.split('/')?.last()}"
                        ))
                        .addOnSuccessListener {
                            hideLoading("submit_opt")
                            clearInput()
                            getData()
                        }
                        .addOnFailureListener {e->
                            hideLoading("submit_opt")
                            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                } else {
                    hideLoading("submit_opt")
                    Toast.makeText(this, it.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun setItem(item: PilihanMateri) {
        this.pilihan = item
        mode = Mode.EDIT

        edt_option_text.setText(item.caption)
        iv_input_img.setPadding(0)
        GlideApp.with(this)
            .asBitmap()
            .load(item.imgUrl)
            .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_materi)))
            .into(iv_input_img)

        tv_audio_name.text = "${item.audioPath?.split('/')?.last()}"
        tv_state_upload_img.visibility = View.GONE
    }

    private fun uploadImageAudio(imageUri: Uri, audioUri: Uri) {

        val path = mFirestore.collection("materi")
            .document(parentKey)
            .collection("pilihan")
        val key = path.document().id

        val rootPath = "materi/$parentTitle-$parentKey/"

        val imgRef = mStorage.getReference("$rootPath$key/${imageUri.path?.split('/')?.last()}")
        imgRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@continueWithTask imgRef.downloadUrl
            }
            .addOnCompleteListener {imgTask ->
                if (imgTask.isSuccessful) {
                    val imgUrl = imgTask.result.toString()

                    val audioRef = mStorage.getReference("$rootPath$key/${audioUri.path?.split('/')?.last()}")
                    audioRef.putFile(audioUri)
                        .continueWithTask { task2 ->
                            if (!task2.isSuccessful) {
                                task2.exception?.let { e ->
                                    throw e
                                }
                            }
                            return@continueWithTask audioRef.downloadUrl
                        }
                        .addOnCompleteListener { audioTask ->
                            if (audioTask.isSuccessful) {
                                val audioUrl = audioTask.result.toString()

                                path.document(key).set(
                                    PilihanMateri(
                                        key,
                                        edt_option_text.text.toString(),
                                        imgUrl,
                                        "$rootPath$key/${imageUri.path?.split('/')?.last()}",
                                        audioUrl,
                                        "$rootPath$key/${audioUri.path?.split('/')?.last()}"
                                    )
                                ).addOnSuccessListener {
                                    hideLoading("submit_opt")
                                    clearInput()
                                    getData()
                                }.addOnFailureListener {
                                    hideLoading("submit_opt")
                                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                            } else {
                                hideLoading("submit_opt")
                                Toast.makeText(this, audioTask.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    hideLoading("submit_opt")
                    Toast.makeText(this, imgTask.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun clearInput() {
        edt_option_text.text.clear()
        edt_option_text.clearFocus()
        imageUri = null
        audioUri = null
        tv_audio_name.text = ""
        tv_state_upload_img.visibility = View.VISIBLE
//        iv_input_img.setPadding(0)
        iv_input_img.setImageResource(0)
        iv_input_img.setBackgroundResource(R.drawable.round_img_input_materi)
        pilihan = null
        mode = Mode.CREATE
    }

    private fun showLoading(type: String) {
        when(type) {
            "submit_opt" -> {
                progress_submit_item.visibility = View.VISIBLE
                btn_simpan_pilihan.visibility = View.INVISIBLE
            }

            "load_opt" -> {
                progress_load_item.visibility = View.VISIBLE
                rv_item_jawaban.visibility = View.GONE
            }

            "submit_all" -> {

            }
        }
    }

    private fun hideLoading(type: String) {
        when(type) {
            "submit_opt" -> {
                progress_submit_item.visibility = View.GONE
                btn_simpan_pilihan.visibility = View.VISIBLE
            }

            "load_opt" -> {
                progress_load_item.visibility = View.GONE
                rv_item_jawaban.visibility = View.VISIBLE
            }

            "submit_all" -> {

            }
        }
    }

    private fun getData() {
        showLoading("load_opt")
        mFirestore.collection("materi")
            .document(parentKey)
            .collection("pilihan")
            .get()
            .addOnSuccessListener {
                hideLoading("load_opt")
                val listData = mutableListOf<PilihanMateri>()
                listData.addAll(it.toObjects(PilihanMateri::class.java))
                if (listData.size > 0) {
                    adapter.setData(listData)
                    rv_item_jawaban.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                hideLoading("load_opt")
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun validate(): Boolean {
        if (mode == Mode.CREATE) {
            if (imageUri == null) {
                Toast.makeText(this, "Anda belum memilih gambar", Toast.LENGTH_LONG).show()
                return false
            }
            if (audioUri == null) {
                Toast.makeText(this, "Anda belum memilih audio", Toast.LENGTH_LONG).show()
                return false
            }
            if (edt_option_text.text.toString().isEmpty()) {
                edt_option_text.error = "Wajib diisi"
                edt_option_text.requestFocus()
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

    @AfterPermissionGranted(rcStorage)
    private fun getAudio() {
        if (hasPermissionStorage()) {
            val intent = Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Pilih Audio"),
                rcAudio
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                resources.getString(R.string.rationale_ask),
                rcAudio,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun hasPermissionCamera(): Boolean = EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    private fun hasPermissionStorage(): Boolean = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data!=null) {
            if (requestCode == rcChoose) {
                if (mode == Mode.EDIT) {
                    mode = Mode.EDIT_FILLED
                }
                tv_state_upload_img.visibility = View.GONE
                iv_input_img.setPadding(0)
                GlideApp.with(this)
                    .asBitmap()
                    .load(data.data)
                    .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_materi)))
                    .into(iv_input_img)
                    this.imageUri = data.data
            } else if (requestCode == rcAudio) {
                if (mode == Mode.EDIT) {
                    mode = Mode.EDIT_FILLED
                }

                this.audioUri = data.data
                tv_audio_name.text = "${audioUri?.path?.split('/')?.last()}"
            }
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