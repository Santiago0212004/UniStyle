package edu.co.icesi.unistyle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import edu.co.icesi.unistyle.databinding.ActivityLoginBinding
import edu.co.icesi.unistyle.domain.model.AppAuthState
import edu.co.icesi.unistyle.domain.model.CustomerDTO
import edu.co.icesi.unistyle.viewmodel.LogInViewmodel

class LoginActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    val viewModel: LogInViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            viewModel.login(
                binding.emailET.text.toString(),
                binding.passET.text.toString()
            )
        }

        binding.noAccountTV.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        viewModel.authStatus.observe(this) {
            when (it) {
                is AppAuthState.Loading -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                is AppAuthState.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                is AppAuthState.Success -> {
                    Toast.makeText(this, "Bienvenido ${it.userID}", Toast.LENGTH_LONG).show()
                }
                is AppAuthState.SuccessLogin -> {
                    Toast.makeText(this, "Bienvenido ${it.userID}", Toast.LENGTH_LONG).show()
                    if (it.role=="worker"){
                        val intent = Intent(this, WorkerSelectActivity::class.java)
                        startActivity(intent)
                    }else if(it.role=="customer"){
                        val intent = Intent(this, MainCustomerActivity::class.java).putExtra("customerId",it.userID)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "No se pudo confirmar tu rol correctamente", Toast.LENGTH_LONG).show()
                    }
                }
                null -> TODO()
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("La contraseña que ingresó es incorrecta")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}