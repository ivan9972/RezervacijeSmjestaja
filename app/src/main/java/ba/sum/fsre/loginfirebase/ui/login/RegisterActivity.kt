package ba.sum.fsre.loginfirebase.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ba.sum.fsre.loginfirebase.viewmodel.LoginViewModel

class RegisterActivity : AppCompatActivity() {

    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel.register("test@mail.com", "123456") { success, error ->
            if (success) {
                Toast.makeText(this, "Registracija OK", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, error ?: "Greška", Toast.LENGTH_SHORT).show()
            }
        }
    }
}