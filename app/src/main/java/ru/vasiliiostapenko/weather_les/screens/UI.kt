package ru.vasiliiostapenko.weather_les.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ru.vasiliiostapenko.weather_les.data.WeatherModel
import ru.vasiliiostapenko.weather_les.ui.theme.LightBlue
import ru.vasiliiostapenko.weather_les.ui.theme.LightBlueNotTr

@Composable
fun MainList (list: List<WeatherModel>, currentDays: MutableState<WeatherModel>){
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(list) { index, item ->ListItem(item, currentDays) }
    }
}
@Composable
fun ListItem(item: WeatherModel, currentDays: MutableState<WeatherModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
            .clickable {
                if (item.hours.isEmpty()) {
                    return@clickable
                }
                currentDays.value = item
            },
        colors = CardDefaults.cardColors(containerColor = LightBlue),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(start = 8.dp, top = 5.dp, bottom = 5.dp)) {
                Text(text = item.time, color = Color.White)
                Text(text = item.condition, color = Color.White)
            }
            Text(text = item.temp_c.ifEmpty { "${item.maxTemp}/${item.minTemp}" } + "°C", color = Color.White, fontSize = 25.sp)
            AsyncImage(
                model = "https:${item.icon}",
                contentDescription = "img",
                modifier = Modifier
                    .size(35.dp)
                    .padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit: (String) -> Unit) {
    val dialogText = remember{
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = { dialogState.value = false },
        confirmButton = {
            TextButton(onClick = {
                onSubmit.invoke(dialogText.value)
                dialogState.value = false
            }) { Text(text = "Ok") }
        },
        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) { Text(text = "Cancel") }
        },
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Enter city name:")
                TextField(value = dialogText.value, onValueChange = {
                    dialogText.value = it
                }, modifier = Modifier.background(Color.Blue))
            }
        },
        containerColor = LightBlueNotTr
    )
}