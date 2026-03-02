package ba.sum.fsre.loginfirebase.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ba.sum.fsre.loginfirebase.database.DatabaseProvider
import ba.sum.fsre.loginfirebase.databinding.ActivityAdminUsersBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var b: ActivityAdminUsersBinding
    private var adminId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAdminUsersBinding.inflate(layoutInflater)
        setContentView(b.root)

        adminId = intent.getIntExtra("KORISNIK_ID", -1)
        if (adminId <= 0) {
            Toast.makeText(this, "Greška: adminId", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        b.btnBack.setOnClickListener { finish() }

        val adapter = AdminUsersAdapter(
            onDelete = { userId -> confirmDeactivate(userId) }
        )

        b.rvUsers.layoutManager = LinearLayoutManager(this)
        b.rvUsers.adapter = adapter

        loadUsers(adapter)
    }

    private fun loadUsers(adapter: AdminUsersAdapter) {
        val db = DatabaseProvider.get(this)
        lifecycleScope.launch {
            b.progress.visibility = View.VISIBLE

            val users = withContext(Dispatchers.IO) {
                db.korisnikDao().findAllActiveUsers(adminId)
            }

            b.progress.visibility = View.GONE
            b.tvCount.text = "Korisnika: ${users.size}"
            adapter.submit(users)
        }
    }

    private fun confirmDeactivate(userId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Deaktivacija korisnika")
            .setMessage("Deaktivirati korisnika #$userId?\nNeće se moći prijaviti, a rezervacije ostaju (CASCADE ne briše).")
            .setPositiveButton("Deaktiviraj") { _, _ -> doDeactivate(userId) }
            .setNegativeButton("Odustani", null)
            .show()
    }

    private fun doDeactivate(userId: Int) {
        val db = DatabaseProvider.get(this)
        lifecycleScope.launch {
            val updated = withContext(Dispatchers.IO) {
                db.korisnikDao().deactivateUserById(userId)
            }

            if (updated > 0) Toast.makeText(this@AdminUsersActivity, "Korisnik deaktiviran.", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this@AdminUsersActivity, "Ne može se deaktivirati (nije USER ili ne postoji).", Toast.LENGTH_LONG).show()

            loadUsers(b.rvUsers.adapter as AdminUsersAdapter)
        }
    }
}