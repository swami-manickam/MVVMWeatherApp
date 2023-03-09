package com.challange.weather.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.challange.weather.databinding.ActivitySplashScreenBinding
import com.challange.weather.presentation.base.BaseActivity
import com.challange.weather.presentation.home.WeatherSearchActivity


private const val DELAY_COUNT: Long = 3000

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateToSearchWeatherActivity()
    }

    private fun startAnimation()
    {
        val rotate = RotateAnimation(0f, 720f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        binding.ivSplashLogo.startAnimation(rotate)
    }

    private fun navigateToSearchWeatherActivity() {
        startAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WeatherSearchActivity::class.java)
            startActivity(intent)
            finish()
        }, DELAY_COUNT)
    }

}