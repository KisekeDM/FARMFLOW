package com.example.farmflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.farmflow.api.RetrofitClient
import com.farmflow.api.models.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val API_KEY = "6d73d768623f0634bd380598c7f8d605" // Replace with your OpenWeatherMap API Key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        // Fetch live weather
        getCurrentLocation()
    }

    private fun fetchWeatherByLocation(lat: Double, lon: Double) {
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val errorTextView: TextView = findViewById(R.id.errorTextView)
        val retryButton: Button = findViewById(R.id.retryButton)

        // Show loading indicator
        progressBar.visibility = View.VISIBLE
        errorTextView.visibility = View.GONE
        retryButton.visibility = View.GONE

        RetrofitClient.instance.getWeatherByCoordinates(lat, lon, API_KEY)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        val tempTextView: TextView = findViewById(R.id.tempTextView)
                        val conditionTextView: TextView = findViewById(R.id.conditionTextView)
                        val weatherIconView: ImageView = findViewById(R.id.weatherIcon)

                        val temperature = weatherResponse?.main?.temp ?: 0.0
                        val condition = weatherResponse?.weather?.get(0)?.description ?: "Unknown"

                        tempTextView.text = "Temp: ${temperature}°C"
                        conditionTextView.text = "Condition: $condition"

                        // Load weather icon
                        val weatherList = weatherResponse?.weather
                        if (!weatherList.isNullOrEmpty()) {
                            val iconCode = weatherList[0].icon
                            val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                            Glide.with(this@MainActivity).load(iconUrl).into(weatherIconView)
                        }


                    } else {
                        when (response.code()) {
                            401 -> showErrorMessage("Invalid API Key. Please check your API settings.", retryButton, errorTextView)
                            404 -> showErrorMessage("Location not found. Please try another location.", retryButton, errorTextView)
                            500 -> showErrorMessage("Server is currently down. Try again later.", retryButton, errorTextView)
                            else -> showErrorMessage("Unexpected error occurred. Please try again.", retryButton, errorTextView)
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE

                    when (t) {
                        is java.net.UnknownHostException -> showErrorMessage("No internet connection. Please check your network.", retryButton, errorTextView)
                        is java.net.SocketTimeoutException -> showErrorMessage("Request timed out. Please try again.", retryButton, errorTextView)
                        else -> showErrorMessage("Something went wrong: ${t.message}", retryButton, errorTextView)
                    }
                }
            })
    }



    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude

                    Log.d("DEBUG", "Latitude: $latitude, Longitude: $longitude")

                    fetchWeatherByLocation(latitude, longitude)
                } ?: run {
                    Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show()
                    Log.e("ERROR", "Location is null")
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showErrorMessage(message: String, retryButton: Button, errorTextView: TextView) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE

        retryButton.setOnClickListener {
            getCurrentLocation()
        }
    }
}
