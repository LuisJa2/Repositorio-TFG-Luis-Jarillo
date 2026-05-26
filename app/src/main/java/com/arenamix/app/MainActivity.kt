package com.arenamix.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arenamix.app.data.SessionManager
import com.arenamix.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar SessionManager con contexto para que pueda leer SharedPreferences
        SessionManager.init(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
