package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var textView: TextView
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textView = binding.textView
        searchView = binding.searchView
        binding.btnExit.setOnClickListener {
            finish()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Perform search when user submits query
                searchCity(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                // Handle text changes, if needed
                return false
            }
        }
        )
        binding.btVar1.setOnClickListener {
            // Fetch weather data when button is clicked
            val query = searchView.query.toString()
            searchCity(query)
        }
    }
    private fun searchCity(cityName: String) {
        // Construct the weather URL with the city name
        val weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&appid=820f3cf1558a68db5403f5d407db9051"

        // Fetch weather data for the entered city
        val queue = Volley.newRequestQueue(this)
        val stringReq = StringRequest(
            Request.Method.GET, weatherUrl,
            { response ->
                // Handle the successful response here
                Log.e("lat", "Response: $response")
                parseJsonResponse(response)
            },
            { error ->
                // Handle errors here
                Log.e("lat", "Error: ${error.message}", error)
                textView.text = "Error: ${error.message}"
            }
        )
        queue.add(stringReq)
    }

    private fun parseJsonResponse(response: String) {
        try {
            // Parse the JSON response and extract the necessary data
            val obj = JSONObject(response)
            val main = obj.getJSONObject("main")
            val temperature = main.getDouble("temp")
            val humidity = main.getInt("humidity")
            val pressure = main.getInt("pressure")
            val weatherArray = obj.getJSONArray("weather")
            val weatherObj = weatherArray.getJSONObject(0)
            var description = weatherObj.getString("description")
            val wind = obj.getJSONObject("wind")
            val windSpeed = wind.getDouble("speed")
            val windDirection = wind.getDouble("deg")
            val clouds = obj.getJSONObject("clouds")
            val cloudiness = clouds.getInt("all")
            val name = obj.getString("name")
            val sys = obj.getJSONObject("sys")
            val country = sys.getString("country")

            // Set the weather information using getString() function
            textView.text = "Weather in $name, $country:\n" +
                    "Description: $description\n" +
                    "Temperature: ${temperature}°C\n" +
                    "Humidity: $humidity%\n" +
                    "Pressure: $pressure hPa\n" +
                    "Wind Speed: $windSpeed m/s\n" +
                    "Wind Direction: $windDirection°\n" +
                    "Cloudiness: $cloudiness%\n"
        } catch (e: JSONException) {
            // Handle JSON parsing errors
            Log.e("lat", "JSON Parsing Error: ${e.message}", e)
            textView.text = "Error parsing JSON response"
        }
    }
}
