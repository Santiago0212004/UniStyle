package edu.co.icesi.unistyle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.co.icesi.unistyle.databinding.ActivityWorkerselectBinding

class WorkerSelectActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityWorkerselectBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

}