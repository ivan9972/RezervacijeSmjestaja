package ba.sum.fsre.usermanagement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var editFullName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var editPassword2: EditText
    private lateinit var btnRegister: Button
    private lateinit var textError: TextView

    private lateinit var repo: AuthRepository
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)

        // Ako je već logiran, nema smisla registracija
        if (session.isLoggedIn()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val db = AppDatabase.getInstance(this)
        repo = AuthRepository(db.userProfileDao())

        editFullName = findViewById(R.id.edit_full_name)
        editEmail = findViewById(R.id.edit_email)
        editPassword = findViewById(R.id.edit_password)
        editPassword2 = findViewById(R.id.edit_password2)
        btnRegister = findViewById(R.id.btn_register)
        textError = findViewById(R.id.text_error)

        // Edge-to-edge insets (root view mora imati id="main")
        val root = findViewById<android.view.View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnRegister.setOnClickListener {
            val fullName = editFullName.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password1 = editPassword.text.toString()
            val password2 = editPassword2.text.toString()

            textError.text = ""

            if (password1 != password2) {
                val msg = "Lozinke se ne podudaraju"
                textError.text = msg
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    repo.register(fullName, email, password1)
                }

                result.onSuccess { userId ->
                    // automatski login nakon registracije
                    session.saveLogin(userId)
                    startActivity(Intent(this@RegisterActivity, WelcomeActivity::class.java))
                    finish()
                }.onFailure { e ->
                    val msg = e.message ?: "Greška"
                    textError.text = msg
                    Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
