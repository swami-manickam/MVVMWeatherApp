package com.challange.weather.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.challange.weather.databinding.ActivitySplashScreenBinding
import com.challange.weather.presentation.home.WeatherSearchActivity
import com.challange.weather.presentation.base.BaseActivity

private const val DELAY_COUNT: Long = 3000

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(this, WeatherSearchActivity::class.java)
            startActivity(intent)
            finish()
        }, DELAY_COUNT)
    }
}