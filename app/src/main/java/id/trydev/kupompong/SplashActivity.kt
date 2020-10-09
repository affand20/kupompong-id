package id.trydev.kupompong

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import id.trydev.kupompong.prefs.SharedPrefs
import id.trydev.kupompong.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        prefs = SharedPrefs(this)

        Handler().postDelayed({
            redirect(prefs)
        }, 2000)
    }

    private fun redirect(prefs: SharedPrefs) {
        if (prefs.userId!=null && prefs.userEmail!=null){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            finish()
        } else {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }
    }
}