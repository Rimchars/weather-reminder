package com.example

import com.example.WeatherReminder.botid
import com.example.WeatherReminder.ct
import com.example.WeatherReminder.tempKey
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileWriter

fun writer() {
    val weatherreminder = mutableListOf(
        mapOf(
            "Bot" to botid,
            "key" to tempKey
        ),
        mapOf(
            "clockTIME" to ct
        )
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