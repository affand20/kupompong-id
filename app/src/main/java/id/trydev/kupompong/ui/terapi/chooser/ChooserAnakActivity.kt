package id.trydev.kupompong.ui.terapi.chooser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.trydev.kupompong.R

class ChooserAnakActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser_anak)

        supportActionBar?.hide()
    }
}