package edu.co.icesi.unistyle

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import edu.co.icesi.unistyle.databinding.ActivitySignupBinding
import edu.co.icesi.unistyle.domain.model.AppAuthState
import edu.co.icesi.unistyle.domain.model.User
import edu.co.icesi.unistyle.domain.model.Worker
import edu.co.icesi.unistyle.repository.EstablishmentRepository
import edu.co.icesi.unistyle.viewmodel.EstablishmentViewmodel
import edu.co.icesi.unistyle.viewmodel.SignUpViewmodel

class SignupActivity : AppCompatActivity() {

    val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    val viewModel: SignUpViewmodel by viewModels()

    val viewmodelEst: EstablishmentViewmodel by viewModels()

    private var items = mutableListOf<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.workerBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.spinnerLabel.visibility = View.VISIBLE
                binding.establishmentSPN.visibility = View.VISIBLE
                viewmodelEst.loadEstablishmentList()

                viewmodelEst.establishmentState.observe(this) { establishments ->
                    establishments?.let {
                        establishments.forEach{
                            items.add(it?.name)
                        }
                        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.establishmentSPN.adapter = adapter
                    }
                }
                /*binding.establishmentSPN.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedItem = items[position]
                        Toast.makeText(this@SignupActivity, "$selectedItem", Toast.LENGTH_SHORT).show()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }*/
            } else {
                binding.establishmentSPN.visibility = View.GONE
                binding.spinnerLabel.visibility = View.INVISIBLE
            }
        }


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
            } else if(binding.workerBtn.isChecked){
                val items = listOf("xd", "xd2", "xd3")
                val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.establishmentSPN.adapter = adapter
                binding.establishmentSPN.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedItem = items [position]
                        Toast.makeText(this@SignupActivity, "$selectedItem", Toast.LENGTH_SHORT).show()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }
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