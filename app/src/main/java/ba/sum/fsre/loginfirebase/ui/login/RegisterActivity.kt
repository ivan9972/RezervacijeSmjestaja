package ba.sum.fsre.loginfirebase.ui.login

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ba.sum.fsre.loginfirebase.databinding.ActivityRegisterBinding
import ba.sum.fsre.loginfirebase.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private val vm: AuthViewModel by viewModels()

    private val nameRegex = Regex("^[\\p{L}]+([ '\\-][\\p{L}]+)*$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        applyCroatianNameInput(b.etIme)
        applyCroatianNameInput(b.etPrezime)

        b.btnRegister.setOnClickListener {
            val ime = cleanName(b.etIme.text?.toString().orEmpty())
            val prezime = cleanName(b.etPrezime.text?.toString().orEmpty())
            val email = b.etEmail.text?.toString().orEmpty().trim()
            val pass = b.etPassword.text?.toString().orEmpty()

            if (ime.isBlank() || prezime.isBlank() || email.isBlank() || pass.isBlank()) {
                toast("Popuni sva polja.")
                return@setOnClickListener
            }

            if (!isValidName(ime)) {
                toast("Ime smije sadržavati samo slova i razmake.")
                return@setOnClickListener
            }

            if (!isValidName(prezime)) {
                toast("Prezime smije sadržavati samo slova i razmake.")
                return@setOnClickListener
            }

            b.etIme.setText(ime)
            b.etPrezime.setText(prezime)

            vm.register(ime, prezime, email, null, pass)
        }

        b.btnBack.setOnClickListener { finish() }

        vm.loading.observe(this) { loading ->
            b.progress.visibility = if (loading == true) View.VISIBLE else View.GONE
            b.btnRegister.isEnabled = loading != true
            b.btnBack.isEnabled = loading != true
        }

        vm.error.observe(this) { msg ->
            if (!msg.isNullOrBlank()) toast(msg)
        }

        vm.korisnik.observe(this) { korisnik ->
            if (korisnik != null) {
                toast("Registracija uspješna. Sada se prijavi.")
                finish()
            }
        }
    }

    private fun applyCroatianNameInput(et: android.widget.EditText) {
        et.filters = arrayOf(allowedNameCharsFilter(), InputFilter.LengthFilter(60))
    }

    private fun allowedNameCharsFilter(): InputFilter {
        return InputFilter { source, _, _, _, _, _ ->
            val s = source?.toString().orEmpty()
            if (s.isEmpty()) return@InputFilter null

            val ok = s.all { ch ->
                ch.isLetter() || ch == ' ' || ch == '-' || ch == '\''
            }

            if (ok) null else ""
        }
    }

    private fun cleanName(raw: String): String {
        return raw
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    private fun isValidName(value: String): Boolean {
        if (value.length !in 2..60) return false
        if (value.startsWith(" ") || value.endsWith(" ")) return false
        if (value.contains("  ")) return false
        return nameRegex.matches(value)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}