package id.trydev.kupompong.ui.panduan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.barteksc.pdfviewer.util.FitPolicy
import id.trydev.kupompong.R
import kotlinx.android.synthetic.main.activity_panduan.*

class PanduanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panduan)

        supportActionBar?.hide()

        btn_back.setOnClickListener {
            finish()
        }

        pdfView.fromAsset("panduan.pdf")
            .enableSwipe(true)
            .pageSnap(true)
            .enableAntialiasing(true)
            .pageFitPolicy(FitPolicy.WIDTH)
            .load()
    }
}