package com.muneshsaini.weathertrack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.muneshsaini.weathertrack.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("alwar")
        searchCity()
    }

    private fun searchCity() {
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    fetchWeatherData(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"07022656576b447ba2eeaed562192c53","metric")
        response.enqueue(object : Callback<WeatherTrack>{
            override fun onResponse(call: Call<WeatherTrack>, response: Response<WeatherTrack>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temp = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise
                    val sunSet  = responseBody.sys.sunset
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unkown"
                    val max_temp = responseBody.main.temp_max
                    val min_temp = responseBody.main.temp_min

                    binding.temperature.text = "$temp °C"
                    binding.weathercon.text = condition
                    binding.maxtemp.text = "Max: $max_temp °C"
                    binding.mintemp.text = "Min: $min_temp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.condition.text = "$condition"
                    binding.sunrise.text = "${time(sunRise.toLong())}"
                    binding.sunset.text = "${time(sunSet.toLong())}"
                    binding.sea.text = "$sealevel hPa"
                    binding.location.text = "$cityName"
                    binding.dayname.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()

                    changeBgAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherTrack>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeBgAccordingToWeatherCondition(conditions:  String) {
        when(conditions){
            "Clear Sky","Sunny","Clear"-> {
                binding.root.setBackgroundResource(R.drawable.clearsky)
                binding.sun.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist"-> {
                binding.root.setBackgroundResource(R.drawable.cloudebg)
                binding.sun.setAnimation(R.raw.cloudsani)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"-> {
                binding.root.setBackgroundResource(R.drawable.peakpx)
                binding.sun.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"-> {
                binding.root.setBackgroundResource(R.drawable.snowbg)
                binding.sun.setAnimation(R.raw.snow)
            }
            "Haze","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.haze)
                binding.sun.setAnimation(R.raw.hazeani)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.cloudybg)
                binding.sun.setAnimation(R.raw.sun)
            }
        }
        binding.sun.playAnimation()
    }
    private fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
       }
    private fun date():String{
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
       }
}