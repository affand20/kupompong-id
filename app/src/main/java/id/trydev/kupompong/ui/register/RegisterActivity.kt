package id.trydev.kupompong.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import id.trydev.kupompong.MainActivity
import id.trydev.kupompong.R
import id.trydev.kupompong.prefs.SharedPrefs
import id.trydev.kupompong.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var prefs:SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        prefs = SharedPrefs(this)

        to_login.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }

        btn_submit.setOnClickListener {
            if (validate()) {
                showLoading()

                mAuth.createUserWithEmailAndPassword(edt_email.text.toString(), edt_password.text.toString())
                    .addOnSuccessListener {
                        hideLoading()
                        prefs.userId = it.user?.uid
                        prefs.userEmail = it.user?.email
                        Toast.makeText(this, "Selamat datang", Toast.LENGTH_LONG).show()
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

    private fun showLoading() {
        progress_bar.visibility = View.VISIBLE
        btn_submit.visibility = View.GONE
        edt_email.isEnabled = false
        edt_password.isEnabled = false
        edt_konfirm_password.isEnabled = false
    }

    private fun hideLoading() {
        progress_bar.visibility = View.GONE
        btn_submit.visibility = View.VISIBLE
        edt_email.isEnabled = true
        edt_password.isEnabled = true
        edt_konfirm_password.isEnabled = true
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
        if (edt_konfirm_password.text.isEmpty()) {
            edt_konfirm_password.requestFocus()
            edt_konfirm_password.error = "Wajib diisi"
            return false
        }
        if (edt_password.text.toString() != edt_konfirm_password.text.toString()) {
            edt_konfirm_password.requestFocus()
            edt_konfirm_password.error = "Password tidak cocok."
            return false
        }
        return true
    }
}