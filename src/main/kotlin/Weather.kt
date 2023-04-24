package com.example

import com.example.WeatherReminder.botid
import com.example.WeatherReminder.ct
import com.example.WeatherReminder.tempKey
import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDateTime

fun weather(city:Any): String {
    val key: String = tempKey.toString()
    var cityName = city.toString()
    cityName= URLEncoder.encode(cityName, "UTF-8")
    val urls = "https://restapi.amap.com/v3/weather/weatherInfo?city=$cityName&key=$key&extensions=base"
    val url = URL(urls)
    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
    conn.requestMethod = "GET"
    conn.connectTimeout = 5000
    conn.readTimeout = 5000
    try {
        val inStream: BufferedInputStream = BufferedInputStream(conn.inputStream)
        val json: JSONObject = JSONObject(inStream.bufferedReader().use { it.readText() })
        val city = json.getJSONArray("lives").getJSONObject(0).getString("city")
        val weather = json.getJSONArray("lives").getJSONObject(0).getString("weather")
        val temperature = json.getJSONArray("lives").getJSONObject(0).getString("temperature")
        val winddirection = json.getJSONArray("lives").getJSONObject(0).getString("winddirection")
        val windpower = json.getJSONArray("lives").getJSONObject(0).getString("windpower")
        val humidity = json.getJSONArray("lives").getJSONObject(0).getString("humidity")
        val reporttime = json.getJSONArray("lives").getJSONObject(0).getString("reporttime")
        return buildString {
            append("城市：")
            append(city)
            append(" \n天气：")
            append(weather)
            append(" \n温度：")
            append(temperature)
            append(" \n风向：")
            append(winddirection)
            append(" \n风力：")
            append(windpower)
            append(" \n湿度：")
            append(humidity)
            append(" \n更新时间：")
            append(reporttime)
        }
    } catch (e: Exception) {
        return "请检查城市名是否正确"
    }
}
suspend fun weatherclock() {
    try {
        while (true) {
            for (i in ct.indices) {
                val time = LocalDateTime.now()
                if (time.hour == ct[i][0].toInt() && time.minute >= ct[i][1].toInt() && time.minute <= ct[i][1].toInt()) {
                    Bot.getInstance(botid!!).getFriend(ct[i][3])?.sendMessage(weather(ct[i][2]))
                    delay(45000)
                }
                delay(1000)
            }
        }
    } catch (e: Exception) {
        Bot.getInstance(botid!!).getFriend(1064979746)?.sendMessage("天气提醒出现异常")
    }
}