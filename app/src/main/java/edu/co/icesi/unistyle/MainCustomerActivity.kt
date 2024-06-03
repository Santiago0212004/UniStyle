package edu.co.icesi.unistyle

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import edu.co.icesi.unistyle.adapters.EstablishmentAdapter
import edu.co.icesi.unistyle.databinding.ActivityMainCustomerBinding
import edu.co.icesi.unistyle.viewmodel.MainCustomerViewModel

class MainCustomerActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainCustomerBinding.inflate(layoutInflater)
    }

    private val viewModel: MainCustomerViewModel by viewModels()

    private val establishmentAdapter = EstablishmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val customerId = currentUser.uid
            val manager = LinearLayoutManager(this)
            binding.establishmentsRV.layoutManager = manager
            binding.establishmentsRV.adapter = establishmentAdapter
            binding.establishmentsRV.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
            )

            viewModel.getLoggedCustomer(customerId)
            viewModel.getEstablishments()

            viewModel.loggedCustomer.observe(this) { customer ->
                when (customer) {
                    null -> Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()
                    else -> {
                        binding.customerNameTV.text = customer.name
                    }
                }
            }

            viewModel.establishments.observe(this) {
                if (it.isEmpty()) {
                    Toast.makeText(this, "No se encontraron establecimientos", Toast.LENGTH_LONG).show()
                } else {
                    establishmentAdapter.establishments = it
                    establishmentAdapter.notifyItemInserted(it.lastIndex)
                }
            }
        } else {
            Toast.makeText(this, "No se ha autenticado ningún usuario", Toast.LENGTH_LONG).show()
        }
    }
}
