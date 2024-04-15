package edu.co.icesi.unistyle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.firestore
import edu.co.icesi.unistyle.databinding.ActivitySignupBinding
import edu.co.icesi.unistyle.domain.model.AppAuthState
import edu.co.icesi.unistyle.domain.model.User
import edu.co.icesi.unistyle.domain.model.Worker
import edu.co.icesi.unistyle.viewmodel.SignupViewmodel

class SignupActivity : AppCompatActivity() {

    val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    val viewModel: SignupViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.signupBtn.setOnClickListener {
            viewModel.signup(
                binding.emailET.text.toString(),
                binding.passET.text.toString(),
                binding.nameET.text.toString(),
                binding.usernameET.text.toString()
            )
        }

        binding.igotaccountBtn.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
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
                    if (binding.workerBtn.isChecked){
                        val email = binding.emailET.text.toString()
                        val password = binding.passET.text.toString()
                        val name = binding.nameET.text.toString()
                        val username = binding.usernameET.text.toString()
                        val worker = Worker(it.userID, email, name, username, password, "",
                            null,null,null,null)
                        Firebase.firestore.collection("worker").document(worker.id).set(worker)
                    } else {
                        val email = binding.emailET.text.toString()
                        val password = binding.passET.text.toString()
                        val name = binding.nameET.text.toString()
                        val username = binding.usernameET.text.toString()
                        val user = User(it.userID, email, name, username, password, "", null,null)
                        Firebase.firestore.collection("customer").document(user.id).set(user)
                    }

                }

                null -> TODO()
            }
        }

    }
}