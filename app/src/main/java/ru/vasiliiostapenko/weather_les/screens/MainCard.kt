package ru.vasiliiostapenko.weather_les.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import ru.vasiliiostapenko.weather_les.R
import ru.vasiliiostapenko.weather_les.data.WeatherModel
import ru.vasiliiostapenko.weather_les.ui.theme.LightBlue

@Composable
fun MainCard(currentDay: MutableState<WeatherModel>, onClickSync: () -> Unit, onClickSearch: () -> Unit) {

    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            //elevation = CardDefaults.cardElevation(3.dp),
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightBlue)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentDay.value.time,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:${currentDay.value.icon}",
                        contentDescription = "img",
                        modifier = Modifier
                            .size(35.dp)
                            .padding(top = 3.dp, end = 8.dp)
                    )
                }
                Text(
                    text = currentDay.value.city,
                    fontSize = 24.sp,
                    color = Color.White
                )
                Text(
                    text = if(currentDay.value.temp_c.isNotEmpty())currentDay.value.temp_c.toFloat().toInt().toString() + "°C"
                    else "${currentDay.value.maxTemp.toFloat().toInt()}°C/${currentDay.value.minTemp.toFloat().toInt()}°C",
                    fontSize = 65.sp,
                    color = Color.White
                )
                Text(
                    text = currentDay.value.condition,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Row( modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        onClickSearch.invoke()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_edit_location_24, ),
                            contentDescription = "im3", tint = Color.White)
                    }
                    Text(
                        text = "${currentDay.value.maxTemp.toFloat().toInt()}°C/${currentDay.value.minTemp.toFloat().toInt()}°C",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    IconButton(onClick = {
                        onClickSync.invoke()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_cached_24, ),
                            contentDescription = "im4", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>){
    val tabList = listOf("HOURS","DAYS")
    var pagerState = rememberPagerState{2}
    var tabIndex = pagerState.currentPage
    var coroutineScope = rememberCoroutineScope()
    Column (modifier = Modifier.padding(start = 5.dp, end = 5.dp).clip(RoundedCornerShape(5.dp)),
        ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { pos ->
                TabRowDefaults.PrimaryIndicator(modifier = Modifier.tabIndicatorOffset(pos[tabIndex]))
            },
            containerColor = LightBlue,
            contentColor = Color.White
        ){
            tabList.forEachIndexed{index, text ->
                Tab(
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(text = text)
                    }
                )
            }
        }
        HorizontalPager(state = pagerState,
            modifier = Modifier.weight(1.0f)) {index ->
            val list = when(index){
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
            MainList(list, currentDay)

        }
    }

}
private fun getWeatherByHours(hours: String): List<WeatherModel>{
    if (hours.isEmpty()) return  listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()){
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString() ,
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                ""
                )
        )
    }
    return list
}