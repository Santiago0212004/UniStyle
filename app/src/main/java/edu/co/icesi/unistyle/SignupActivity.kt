package edu.co.icesi.unistyle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.co.icesi.unistyle.databinding.ActivitySignupBinding
import edu.co.icesi.unistyle.domain.model.AppAuthState
import edu.co.icesi.unistyle.domain.model.User
import edu.co.icesi.unistyle.domain.model.Worker
import edu.co.icesi.unistyle.viewmodel.SignUpViewmodel

class SignupActivity : AppCompatActivity() {

    val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    val viewModel: SignUpViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.signupBtn.setOnClickListener {
            if(!binding.workerBtn.isChecked){
                viewModel.signupUser(
                    User(
                        "",
                        binding.emailET.text.toString(),
                        binding.nameET.text.toString(),
                        binding.usernameET.text.toString(),
                        "",
                        null,
                        null),
                    binding.passET.text.toString()
                )
            } else{
                viewModel.signupWorker(
                    Worker(
                        "",
                        binding.emailET.text.toString(),
                        binding.nameET.text.toString(),
                        binding.usernameET.text.toString(),
                        "",
                        null,
                        null,
                        null,
                        null),
                    binding.passET.text.toString()
                )
            }
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

                }

                null -> TODO()
            }
        }

    }
}