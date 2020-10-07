package com.trapezoidlimited.groundforce.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trapezoidlimited.groundforce.databinding.ActivityMainBinding
import android.widget.Toast
import com.trapezoidlimited.groundforce.R
import com.trapezoidlimited.groundforce.navigation.AppNavigator
import com.trapezoidlimited.groundforce.navigation.AppNavigatorImpl
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inflate layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
}

