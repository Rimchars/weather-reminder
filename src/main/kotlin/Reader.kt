package com.example

import org.yaml.snakeyaml.Yaml
import java.io.File

val file = File("config${File.separator}com.example.weather-reminder${File.separator}weather-reminder.yml")
fun keyreader(): String{
    val yaml = Yaml()
    try {
        val list = yaml.load<List<Map<String, Any>>>(file.readText())
        return list[0]["key"] as String
    }
    catch (e: Exception) {
        return "123456789"
    }
}
fun clockreader(): List<List<Long>> {
    val yaml = Yaml()
    try {
        val list = yaml.load<List<Map<String, Any>>>(file.readText())
        return list[1]["clockTIME"] as List<List<Long>>
    } catch (e: Exception) {
        return listOf(listOf(0,0,0,0))
    }
}