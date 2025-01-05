package ru.vasiliiostapenko.weather_les

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import ru.vasiliiostapenko.weather_les.data.WeatherModel
import ru.vasiliiostapenko.weather_les.screens.DialogSearch
import ru.vasiliiostapenko.weather_les.screens.MainCard
import ru.vasiliiostapenko.weather_les.screens.TabLayout
import ru.vasiliiostapenko.weather_les.ui.theme.Weather_lesTheme
const val apikey = "PASTE YOUR API KEY HERE"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Weather_lesTheme {
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                val dialogState = remember {
                    mutableStateOf(false)
                }
                val currentDay = remember {
                    mutableStateOf(WeatherModel("","","0","","","0","0",""))
                }
                var thread = Thread{
                    getData("Omsk", this, daysList, currentDay)
                }
                thread.start()
                if(dialogState.value){
                    DialogSearch(dialogState, onSubmit = {
                        var thread = Thread{
                            getData(it, this, daysList, currentDay)
                        }
                        thread.start()
                    })
                }
                Image(
                    painter = painterResource(id = R.drawable.mainbg),
                    contentDescription = "im1",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.8f),
                    contentScale = ContentScale.Crop
                )
                Column {
                    MainCard(currentDay, onClickSync = {
                        var thread = Thread { getData("Omsk", this@MainActivity, daysList, currentDay) }
                        thread.start()
                    }, onClickSearch = {
                        dialogState.value = true
                    })
                    TabLayout(daysList, currentDay)
                }

            }
        }
    }
}

private fun getData(
    city: String,
    context: Context,
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
){
    val url = "https://api.weatherapi.com/v1/forecast.json?key="+
            apikey+
            "&q="+
            city+
            "&days=3&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
            respounse ->
            Log.d("MainActivity", "VolleyResponse: ${respounse}")
            val list = getWeatherByDay(respounse)
            daysList.value = list
            if (list.isNotEmpty()){
                currentDay.value = list[0]
            }
        },
        {
            Log.d("MainActivity", "VolleyError: ${it}")
        }
    )
    queue.add(sRequest)
}
private fun getWeatherByDay(respounse: String) : List<WeatherModel>{
    if(respounse.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(respounse)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()){
        val item = days[i] as JSONObject
        list.add(WeatherModel(
            city,
            item.getString("date"),
            "",
            item.getJSONObject("day").getJSONObject("condition").getString("text"),
            item.getJSONObject("day").getJSONObject("condition").getString("icon"),
            item.getJSONObject("day").getString("maxtemp_c"),
            item.getJSONObject("day").getString("mintemp_c"),
            item.getJSONArray("hour").toString()
        ))
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        temp_c = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}