package com.challange.weather.presentation.home

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import coil.load
import com.challange.weather.R
import com.challange.weather.app.AppConstants
import com.challange.weather.data.api.ApiConstants
import com.challange.weather.data.di.modules.DatabaseBuilder
import com.challange.weather.data.model.WeatherDetail
import com.challange.weather.data.response.WeatherDataModel
import com.challange.weather.data.response.base.ResponseState.*
import com.challange.weather.databinding.ActivityLoginBinding
import com.challange.weather.presentation.base.BaseActivity
import com.challange.weather.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherSearchActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        getCityWeather()

    }

    private fun setupUI() {

        binding.etFindCityWeather.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val cityName =(view as EditText).text.toString()
                if(!cityName.isNullOrEmpty() && cityName.length > 3)
                    fetchWeatherDetail(cityName)
                else
                    showToast(getString(R.string.enter_city_name))
                binding.etFindCityWeather.setText("")
            }
            false
        }
    }



    private fun getCityWeather() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                getWeatherDetailFromDb()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private suspend fun getWeatherDetailFromDb() {

        var detail : List<WeatherDetail>? = DatabaseBuilder.getInstance(this).weatherDetailDao().fetchAllWeatherDetails()

        if(detail!=null)
            showWeatherDetail(detail.first())
    }

    private fun fetchWeatherDetail(cityName: String) {
        weatherViewModel.fetchWeatherDetail(cityName).observe(this, Observer { state ->
            when (state) {
                is Loading -> {
                    val isLoading = state.isLoading as Boolean
                    if (isLoading) {
                        showLoading()
                    } else {
                        hideLoading()
                    }
                }
                is Success -> {
                    val weatherDataModel = state.data as WeatherDataModel
                    addCityWeather(weatherDataModel)
                }
                is Failure -> {
                    val throwable = state.error
                    showToast(throwable.message ?: "")
                }
            }
        })
    }


    private fun showWeatherDetail(weatherDataModel : WeatherDetail)
    {

        GlobalScope.launch(Dispatchers.Main) {
            try {
                weatherDataModel.let { weatherDetail ->
                    val iconCode = weatherDetail?.icon?.replace("n", "d")
                    binding.ivWeatherSymbol.load(ApiConstants.WEATHER_API_IMAGE_ENDPOINT + "${iconCode}@2x.png")
                    binding.tvTodayDate.text = DateTimeUtils.getCurrentDateTime(AppConstants.DATE_FORMAT)
                    binding.tvTemperature.text = weatherDetail?.temp.toString()
                    binding.tvLabelName.text = weatherDetail?.weatherType
                    binding.tvLabelWind.text = "${getString(R.string.wind)}  ${weatherDetail?.speed.toString()} ${getString(R.string.wind_notation)}"
                    binding.tvLabelPressure.text = "${getString(R.string.pressure)}  ${weatherDetail?.pressure.toString()} ${getString(R.string.pressure_notation)}"
                    binding.tvLabelHumidity.text = "${getString(R.string.humidity)}  ${weatherDetail?.humidity.toString()} ${getString(R.string.humidity_notation)}"
                    binding.tvLabelSunRise.text = "${getString(R.string.sunrise)}  ${weatherDetail?.sunRise.toString()}"
                    binding.tvLabelSunSet.text = "${getString(R.string.sunset)}  ${weatherDetail?.sunSet.toString()}"

                    binding.tvLabelSearchForCity.text =
                        "${weatherDetail?.cityName?.capitalize()}, ${weatherDetail?.countryName}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun addCityWeather(weatherData: WeatherDataModel) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                addWeatherDetailIntoDb(weatherData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun addWeatherDetailIntoDb(weatherData: WeatherDataModel) {
        val weatherDetail = WeatherDetail()
        weatherDetail.icon = weatherData.weather?.first()?.icon
        weatherDetail.weatherType = weatherData.weather?.first()?.main
        weatherDetail.cityName = weatherData.name?.toLowerCase()
        weatherDetail.countryName = weatherData.sys?.country
        weatherDetail.temp = weatherData.main?.temp
        weatherDetail.dateTime = DateTimeUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1)
        weatherDetail.speed = weatherData.main?.temp
        weatherDetail.pressure = weatherData.main?.pressure
        weatherDetail.humidity = weatherData.main?.humidity
        weatherDetail.sunRise = weatherData.sys?.sunrise
        weatherDetail.sunSet = weatherData.sys?.sunset
        DatabaseBuilder.getInstance(this).weatherDetailDao().addWeather(weatherDetail)

        getCityWeather()
    }

}