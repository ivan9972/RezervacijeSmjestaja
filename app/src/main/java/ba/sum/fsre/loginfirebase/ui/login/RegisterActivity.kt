package ba.sum.fsre.loginfirebase.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ba.sum.fsre.loginfirebase.databinding.ActivityRegisterBinding
import ba.sum.fsre.loginfirebase.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private val vm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnRegister.setOnClickListener {
            val ime = b.etIme.text.toString().trim()
            val prezime = b.etPrezime.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString()

            if (ime.isBlank() || prezime.isBlank() || email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Popuni sva polja.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.register(ime, prezime, email, null, pass)
        }

        b.btnBack.setOnClickListener { finish() }

        vm.loading.observe(this) { loading ->
            b.progress.visibility = if (loading) View.VISIBLE else View.GONE
            b.btnRegister.isEnabled = !loading
            b.btnBack.isEnabled = !loading
        }

        vm.error.observe(this) { msg ->
            if (!msg.isNullOrBlank()) Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        vm.korisnik.observe(this) { korisnik ->
            if (korisnik != null) {
                Toast.makeText(this, "Registracija OK. Sad se prijavi.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}