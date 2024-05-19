package edu.co.icesi.unistyle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import edu.co.icesi.unistyle.databinding.ActivityWorkerProfileBinding
import icesi.edu.co.icesiapp241.viewmodel.WorkerProfileViewModel

class WorkerProfileActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityWorkerProfileBinding.inflate(layoutInflater)
    }

    val viewmodel: WorkerProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Firebase.auth.currentUser?.let {
            Log.e(">>>>>>", it.toString())
            viewmodel.loadUser()

            viewmodel.userState.observe(this) {
                binding.profileMailTV.text = it.email
                binding.profileNameTV.text = it.name
                it.picture?.let {
                    Glide.with(this@WorkerProfileActivity).load(it).into(binding.profileImageIV)
                }
                Log.e(">>>", it.picture.toString())
            }



        } ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }



}