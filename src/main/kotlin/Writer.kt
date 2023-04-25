package com.example

import com.example.WeatherReminder.botid
import com.example.WeatherReminder.ct
import com.example.WeatherReminder.tempKey
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileWriter
import java.time.Clock

fun writer() {
    val weatherreminder = listOf(
        mapOf(
            "Bot" to botid,
            "key" to tempKey
        ),
        mapOf(
            "clockTIME" to ct
        ),
    )
    val rootDir = File(".").absoluteFile.parentFile // 获取当前程序所在目录的上级目录
    val configDir = File(rootDir, "config") // 创建 config 目录
    val weatherReminderDir = File(configDir, "com.example.weather-reminder") // 创建 WeatherReminder 目录
    val weatherReminderFile = File(weatherReminderDir, "weather-reminder.yml") // 创建 weather-reminder.yml 文件
    if (!configDir.exists()) configDir.mkdirs()
    if (!weatherReminderDir.exists()) weatherReminderDir.mkdirs()
    if (!weatherReminderFile.exists()) weatherReminderFile.createNewFile()
    val yaml = Yaml()
    FileWriter("config${File.separator}com.example.weather-reminder${File.separator}weather-reminder.yml").use { writer ->
        yaml.dump(weatherreminder, writer)
    }
}
fun clockcheck(clock: List<Long>): Boolean {
    val yaml = Yaml()
    for (i in yaml.load<List<Map<String, Any>>>(File("config${File.separator}com.example.weather-reminder${File.separator}weather-reminder.yml").readText())[1]["clockTIME"] as List<List<Long>>) {
        if (i[0] == clock[0] && i[1] == clock[1] && i[2] == clock[2] && i[3] == clock[3]) {
            return true
            break
        }
    }
    return false
}