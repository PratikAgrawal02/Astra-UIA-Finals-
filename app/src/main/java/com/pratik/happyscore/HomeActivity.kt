package com.pratik.happyscore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.pratik.happyscore.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        //Hides action bar
        supportActionBar?.hide()

        val bottomNavigationView = _binding.bottomNav
        val navController: NavController = findNavController(R.id.fragmentContainerView)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.symptom,R.id.stats,R.id.medicine, R.id.appointment, R.id.settings))
//        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)
    }
}