package id.trydev.kupompong.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import id.trydev.kupompong.MainActivity
import id.trydev.kupompong.R
import id.trydev.kupompong.prefs.SharedPrefs
import id.trydev.kupompong.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_submit
import kotlinx.android.synthetic.main.activity_login.edt_email
import kotlinx.android.synthetic.main.activity_login.edt_password
import kotlinx.android.synthetic.main.activity_login.progress_bar

class LoginActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var prefs: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        prefs = SharedPrefs(this)

//        redirect(prefs)

        to_register.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
            finish()
        }

        btn_submit.setOnClickListener {
            if (validate()) {
                showLoading()

                mAuth.signInWithEmailAndPassword(edt_email.text.toString(), edt_password.text.toString())
                    .addOnSuccessListener {
                        hideLoading()
                        prefs.userId = it.user?.uid
                        prefs.userEmail = it.user?.email
                        Toast.makeText(this, "Selamat Datang", Toast.LENGTH_LONG).show()
                        startActivity(
                            Intent(this, MainActivity::class.java)
                        )
                        finish()
                    }
                    .addOnFailureListener {
                        hideLoading()
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

//    private fun redirect(prefs: SharedPrefs) {
//        if (prefs.userId!=null && prefs.userEmail!=null){
//            startActivity(
//                Intent(this, MainActivity::class.java)
//            )
//            finish()
//        }
//    }

    private fun showLoading() {
        progress_bar.visibility = View.VISIBLE
        btn_submit.visibility = View.GONE
        edt_email.isEnabled = false
        edt_password.isEnabled = false
    }

    private fun hideLoading() {
        progress_bar.visibility = View.GONE
        btn_submit.visibility = View.VISIBLE
        edt_email.isEnabled = true
        edt_password.isEnabled = true
    }

    private fun validate(): Boolean {
        if (edt_email.text.isEmpty()) {
            edt_email.requestFocus()
            edt_email.error = "Wajib diisi"
            return false
        }
        if (edt_password.text.isEmpty()) {
            edt_password.requestFocus()
            edt_password.error = "Wajib diisi"
            return false
        }

        return true
    }
}