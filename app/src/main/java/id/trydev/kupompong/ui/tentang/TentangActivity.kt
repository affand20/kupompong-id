package id.trydev.kupompong.ui.tentang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.trydev.kupompong.R
import kotlinx.android.synthetic.main.activity_tentang.*

class TentangActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang)

        supportActionBar?.hide()

        btn_back.setOnClickListener {
            finish()
        }

    }
}