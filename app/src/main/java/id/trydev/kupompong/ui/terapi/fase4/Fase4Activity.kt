package id.trydev.kupompong.ui.terapi.fase4

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.kupompong.R
import id.trydev.kupompong.adapter.JawabanFaseAdapter
import id.trydev.kupompong.model.Anak
import id.trydev.kupompong.model.Fase
import id.trydev.kupompong.model.Materi
import id.trydev.kupompong.model.PilihanMateri
import id.trydev.kupompong.utils.GlideApp
import kotlinx.android.synthetic.main.activity_fase4.*
import kotlinx.android.synthetic.main.activity_fase4.btn_back
import kotlinx.android.synthetic.main.activity_fase4.btn_play
import kotlinx.android.synthetic.main.activity_fase4.rv_item_jawaban
import kotlinx.android.synthetic.main.activity_tambah_materi.*

class Fase4Activity : AppCompatActivity() {

    private lateinit var anak: Anak
    private lateinit var materi: Materi
    private lateinit var fase: Fase

    private val mFirestore = FirebaseFirestore.getInstance()
    private lateinit var player: MediaPlayer
    private lateinit var adapter: JawabanFaseAdapter

    private var pilihanMateri: PilihanMateri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fase4)

        supportActionBar?.hide()

        val listAudio = listOf(R.raw.aku, R.raw.mau)
        var pos = 0

        player = MediaPlayer()

        anak = intent?.getSerializableExtra("anak") as Anak
        materi = intent?.getSerializableExtra("materi") as Materi
        fase = intent?.getSerializableExtra("fase") as Fase

        if (fase.level == 3) {
            btn_timer.visibility = View.VISIBLE
        }

        adapter = JawabanFaseAdapter(this, fase) {
            this.pilihanMateri = it
            pilihan_jawaban.visibility = View.VISIBLE

            Log.d("PILIHAN_MATERI", "$it")
            Log.d("IMAGE_URL", it.imgUrl.toString())

            iv_img.setPadding(0)
            GlideApp.with(this)
                .asBitmap()
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("LOAD_FAILED", e?.localizedMessage.toString())
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("IMAGE_READY", it.imgUrl.toString())
                        return false
                    }

                })
                .load(it.imgUrl)
                .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_materi)))
                .into(iv_img)

            tv_caption.text = it.caption
        }

        rv_item_jawaban.layoutManager = GridLayoutManager(this,2)
        rv_item_jawaban.adapter = adapter

        btn_reset.setOnClickListener {
            this.pilihanMateri = null
            pilihan_jawaban.visibility = View.GONE
        }

        getJawaban(materi.id.toString())

        btn_back.setOnClickListener {
            finish()
        }

        tv_fase4_title.text = "Fase ${fase.fase} Level ${fase.level}"

        btn_play.setOnClickListener {
            if (validate()) {
                player.reset()
                player.setDataSource(this, Uri.parse(RES_PREFIX+listAudio[pos]))

                player.setOnPreparedListener {
                    player.start()
                }

                player.setOnCompletionListener {
                    player.reset()
                    pos++
                    when(pos) {
                        in 0..1 -> {
                            player.setDataSource(this, Uri.parse(RES_PREFIX+listAudio[pos]))
                            player.prepare()
                        }

                        2 -> {
                            player.setDataSource(pilihanMateri?.audioUrl)
                            player.prepare()
                        }

                        else -> {
                            if (player.isPlaying) {
                                player.stop()
                            }
                            player.reset()
                            pos = 0
                        }
                    }
                }
                player.prepare()
            }
//            if (!player.isPlaying) {
//                player.reset()
//                player.setDataSource(this,
//                    Uri.parse(RES_PREFIX+R.raw.aku)
//                )
//                player.prepare()
//                player.setOnCompletionListener {
//
//                }
//            }
        }
        
    }

    private fun validate(): Boolean {
        if (this.pilihanMateri == null) {
            Toast.makeText(this, "Mohon pilih jawaban terlebih dahulu", Toast.LENGTH_LONG).show()
            return false
        }

        return true

    }

    private fun getJawaban(materiId: String) {
        showLoading()
        mFirestore.collection("materi")
            .document(materiId)
            .collection("pilihan")
            .get()
            .addOnSuccessListener {
                hideLoading()
                val listJawaban = mutableListOf<PilihanMateri>()
                listJawaban.addAll(it.toObjects(PilihanMateri::class.java))
                adapter.setData(listJawaban)
            }
            .addOnFailureListener {
                hideLoading()
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun showLoading() {
        progress_bar.visibility = View.VISIBLE
        rv_item_jawaban.visibility = View.GONE
    }

    private fun hideLoading() {
        progress_bar.visibility = View.GONE
        rv_item_jawaban.visibility = View.VISIBLE
    }

    companion object {
        const val RES_PREFIX = "android.resource://id.trydev.kupompong/"
    }

}