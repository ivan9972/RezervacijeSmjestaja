package ba.sum.fsre.loginfirebase.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ba.sum.fsre.loginfirebase.databinding.ActivityLoginBinding
import ba.sum.fsre.loginfirebase.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private val vm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString()

            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Unesi email i lozinku.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.login(email, pass)
        }

        b.btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        vm.loading.observe(this) { loading ->
            b.progress.visibility = if (loading) View.VISIBLE else View.GONE
            b.btnLogin.isEnabled = !loading
            b.btnGoRegister.isEnabled = !loading
        }

        vm.error.observe(this) { msg ->
            if (!msg.isNullOrBlank()) Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }


        vm.korisnik.observe(this) { korisnik ->
            if (korisnik != null) {
                val i = Intent(this, WelcomeActivity::class.java)
                i.putExtra("KORISNIK_ID", korisnik.id)
                startActivity(i)
                finish()
            }
        }
    }
}