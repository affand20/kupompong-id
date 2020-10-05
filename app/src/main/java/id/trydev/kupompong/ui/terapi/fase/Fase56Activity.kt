package id.trydev.kupompong.ui.terapi.fase

import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
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
import id.trydev.kupompong.ui.progress.tambah.InputProgressTerapiActivity
import id.trydev.kupompong.utils.CustomItemDecoration
import id.trydev.kupompong.utils.GlideApp
import kotlinx.android.synthetic.main.activity_fase56.*
import kotlinx.android.synthetic.main.activity_fase56.btn_back
import kotlinx.android.synthetic.main.activity_fase56.btn_play
import kotlinx.android.synthetic.main.activity_fase56.btn_reset
import kotlinx.android.synthetic.main.activity_fase56.btn_timer
import kotlinx.android.synthetic.main.activity_fase56.progress_bar
import kotlinx.android.synthetic.main.activity_fase56.rv_item_jawaban
import kotlinx.android.synthetic.main.activity_fase56.pilihan_jawaban_3
import kotlinx.android.synthetic.main.activity_fase56.tv_timer

class Fase56Activity : AppCompatActivity() {

    private lateinit var anak: Anak
    private lateinit var materi: Materi
    private lateinit var fase: Fase

    private val mFirestore = FirebaseFirestore.getInstance()
//    private var taskDownload: Task<FileDownloadTask.TaskSnapshot>? = null
    private lateinit var player: MediaPlayer
    private lateinit var adapter: JawabanFaseAdapter

    private var pilihanMateri: PilihanMateri? = null

    private var second = 0
    private var minute = 0

    private val handler = Handler()
    private var startTime: Long = 0
    private var timerOn: Boolean = false
    private val timerRun = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - startTime
            second = (millis/1000).toInt()
            minute = second/60
            second %= 60

            tv_timer.text = String.format("%02d:%02d", minute, second)
            handler.postDelayed(this, 500)
        }

    }

    private var isPlaySoal: Boolean = false

    private val soundLv5 = arrayOf(
        R.raw.mau_makan_apa,
        R.raw.olahraga_apa
    )

    private val soundLv6 = arrayOf(
        R.raw.olahraga_apa,
        R.raw.mau_makan_apa
    )

    private val listAudio = MutableList(3) { 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fase56)

        supportActionBar?.hide()
        var pos = 0

        player = MediaPlayer()
        player.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        anak = intent?.getSerializableExtra("anak") as Anak
        materi = intent?.getSerializableExtra("materi") as Materi
        fase = intent?.getSerializableExtra("fase") as Fase

        tv_fase4_title.text = "Fase ${fase.fase} Level ${fase.level}"

        if (fase.fase == 5) {
            val listPertanyaan = resources.getStringArray(R.array.list_pertanyaan_lv5)
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listPertanyaan)
            spinnner_pertanyaan.adapter = arrayAdapter
        }
        if (fase.fase == 6) {
            val listPertanyaan = resources.getStringArray(R.array.list_pertanyaan_lv6)
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listPertanyaan)
            spinnner_pertanyaan.adapter = arrayAdapter
        }

        if (fase.level == 3) {
            btn_timer.visibility = View.VISIBLE
            tv_timer.visibility = View.VISIBLE
        }

        adapter = JawabanFaseAdapter(this, fase) {
            this.pilihanMateri = it

//            val tempFile = File.createTempFile(it.caption.toString(), "mp3")
//
//            FirebaseStorage.getInstance().getReference(it.audioPath.toString())
//                .getFile(tempFile)
//                .addOnCompleteListener { task ->
//                    this.taskDownload = task
//                }
//
//            Log.d("PILIHAN_MATERI", "$it")
//            Log.d("IMAGE_URL", it.imgUrl.toString())

            if (fase.level == 1) {
                pilihan_jawaban.visibility = View.VISIBLE
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
            } else {
                pilihan_jawaban_3.text = it.caption
            }
        }

        rv_item_jawaban.layoutManager = GridLayoutManager(this,2)
        rv_item_jawaban.addItemDecoration(CustomItemDecoration(50))
        rv_item_jawaban.adapter = adapter
        getJawaban(materi.id.toString())

        btn_reset.setOnClickListener {
            if (player.isPlaying) {
                player.stop()
                player.reset()
            }
            this.pilihanMateri = null
            if (fase.level == 1) {
                pilihan_jawaban.visibility = View.GONE
            } else {
                pilihan_jawaban_3.text = "..."
            }
            pilihan_jawaban_1.text = "..."
            pilihan_jawaban_2.text = "..."
        }


        btn_back.setOnClickListener {
            finish()
        }

        btn_play_soal.setOnClickListener {

            isPlaySoal = true

            if (player.isPlaying) {
                player.stop()
            }
            player.reset()

            if (fase.fase == 5) {
                player.setDataSource(
                    this,
                    Uri.parse(RES_PREFIX+soundLv5[spinnner_pertanyaan.selectedItemPosition])
                )
            }

            if (fase.fase == 6) {
                player.setDataSource(
                    this,
                    Uri.parse(RES_PREFIX+soundLv6[spinnner_pertanyaan.selectedItemPosition])
                )
            }

            player.setOnPreparedListener{
                player.start()
            }

            player.prepare()

        }

        btn_play.setOnClickListener {
            if (validate()) {

                isPlaySoal = false

                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                player.setDataSource(this, Uri.parse(RES_PREFIX+listAudio[pos]))

                player.setOnPreparedListener {
                    player.start()
                }

                player.setOnBufferingUpdateListener { mp, i ->
                    Log.d("BUFFERING", "progress: $i \n MediaPlayer status: ${mp.isPlaying}")
                }

                player.setOnErrorListener { _, i, i2 ->
                    Log.d("ERROR", "what: $i, extra: $i2")
                    false
                }

                player.setOnCompletionListener {
                    if (!isPlaySoal) {
                        if (this.listAudio[0] != 0 && this.listAudio[1] != 0) {
                            player.reset()
                            pos++
                            when(pos) {
                                in 0..1 -> {
                                    player.setDataSource(this, Uri.parse(RES_PREFIX+listAudio[pos]))
                                    player.prepare()
                                }

                                2 -> {
                                    player.setDataSource(pilihanMateri?.audioUrl)
                                    player.prepareAsync()
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
                    } else {
                        isPlaySoal = false
                    }
                }

                player.prepare()

            }
        }

        btn_done.setOnClickListener {
            startActivity(
                Intent(this, InputProgressTerapiActivity::class.java)
                    .putExtra("anak", this.anak)
            )

            finish()
        }

        btn_timer.setOnClickListener {
            if (this.timerOn) {
                this.timerOn = false
                this.startTime = 0
                handler.removeCallbacks(timerRun)
                tv_timer.text = String.format("%02d:%02d", minute, second)
            } else {
                timerOn = true
                this.startTime = System.currentTimeMillis()
                handler.postDelayed(timerRun, 0)
            }
        }

        ans_aku.setOnClickListener {
            pilihan_jawaban_1.text = ans_aku.text.toString()
            listAudio[0] = R.raw.aku
        }

        ans_saya.setOnClickListener {
            pilihan_jawaban_1.text = ans_saya.text.toString()
            listAudio[0] = R.raw.saya
        }

        ans_kamu.setOnClickListener {
            pilihan_jawaban_1.text = ans_kamu.text.toString()
            listAudio[0] = R.raw.kamu
        }

        ans_mau.setOnClickListener {
            pilihan_jawaban_2.text = ans_mau.text.toString()
            listAudio[1] = R.raw.mau
        }

        ans_ingin.setOnClickListener {
            pilihan_jawaban_2.text = ans_ingin.text.toString()
            listAudio[1] = R.raw.ingin
        }

        ans_tdk_ingin.setOnClickListener {
            pilihan_jawaban_2.text = ans_tdk_ingin.text.toString()
            listAudio[1] = R.raw.tidak_ingin
        }
        
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(timerRun)
    }

    private fun validate(): Boolean {
        if (this.listAudio[0] == 0) {
            Toast.makeText(this, "Mohon pilih jawaban di kotak pertama terlebih dahulu", Toast.LENGTH_LONG).show()
            return false
        }
        if (this.listAudio[1] == 0) {
            Toast.makeText(this, "Mohon pilih jawaban di kotak kedua terlebih dahulu", Toast.LENGTH_LONG).show()
            return false
        }
        if (this.pilihanMateri == null) {
            Toast.makeText(this, "Mohon pilih jawaban di kotak ketiga terlebih dahulu", Toast.LENGTH_LONG).show()
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