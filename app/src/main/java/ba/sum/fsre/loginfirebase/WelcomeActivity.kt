package ba.sum.fsre.usermanagement

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeActivity : AppCompatActivity() {

    private lateinit var textWelcome: TextView
    private lateinit var buttonLogout: Button
    private lateinit var listProfiles: ListView

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)

        // Ako nije logiran, vrati ga na login (zaštita)
        if (!session.isLoggedIn()) {
            goToLogin(clearBackStack = true)
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        textWelcome = findViewById(R.id.text_welcome)
        buttonLogout = findViewById(R.id.button_logout)
        listProfiles = findViewById(R.id.list_profiles)

        // Insets (root view mora imati id="main")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Učitaj user profil iz Room baze i prikaži ime/email
        val db = AppDatabase.getInstance(this)
        val dao = db.userProfileDao()

        val userId = session.getUserId()

        lifecycleScope.launch {
            // sve iz baze na IO threadu
            val (currentUser, profiles) = withContext(Dispatchers.IO) {
                val cu = dao.findById(userId)
                val all = dao.getAllProfiles()
                cu to all
            }

            if (currentUser == null) {
                session.logout()
                goToLogin(clearBackStack = true)
                return@launch
            }

            textWelcome.text = "Welcome, ${currentUser.fullName}!"

            val items = profiles.map { "${it.fullName} (${it.email})" }
            val adapter = ArrayAdapter(
                this@WelcomeActivity,
                android.R.layout.simple_list_item_1,
                items
            )
            listProfiles.adapter = adapter
        }


        buttonLogout.setOnClickListener {
            session.logout()
            goToLogin(clearBackStack = true)
        }
    }

    private fun goToLogin(clearBackStack: Boolean) {
        val intent = Intent(this, LoginActivity::class.java)
        if (clearBackStack) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
