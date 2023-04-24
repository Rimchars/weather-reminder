package com.example

import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.info
import java.io.File

object WeatherReminder : KotlinPlugin(
    JvmPluginDescription(
        id = "com.example.weather-reminder",
        name = "WeatherReminder",
        version = "0.1.0",
    ) {
        author("axi")
    }
) {
    var tempKey: String? = null
    var ct:List<List<Long>> = clockreader()
    var botid: Long? = null
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        //配置文件目录 "${dataFolder.absolutePath}
        globalEventChannel().subscribeAlways<FriendMessageEvent> {
            if (message.content.contains("帮助")) {
                toCommandSender().sendMessage("指令\n查询城市天气：天气 <城市名>\n天气提醒闹钟：时间 <时:分> <城市编码>\n设置你的key：key <你的key>")
                if (tempKey == null||tempKey=="123456789") {
                    toCommandSender().sendMessage("你还没有设置key.请使用key <你的key>来设置key")
                }
            }
            if (message.content.contains("天气 ")) {
                if (tempKey == null||tempKey=="123456789") {
                    toCommandSender().sendMessage("你还没有设置key.请使用key <你的key>来设置key")
                }
                else {
                    val regex = Regex("天气 ")
                    //citi转换成utf8
                    var citi = regex.replace(message.content, "")
                    toCommandSender().sendMessage(weather(citi))
                }
            }
            if (message.content.contains("key")) {
                tempKey = message.content.split(" ")[1]
                writer()
                toCommandSender().sendMessage("你的key:"+keyreader()+"设置成功")
            }
            if (message.content.contains("时间 ")) {
                if (tempKey == null || tempKey == "123456789") {
                    toCommandSender().sendMessage("你还没有设置key.请使用key <你的key>来设置key")
                } else {
                    val regex = Regex("\\d+:\\d+ \\d+")
                    val numbers: List<Long> =
                        regex.findAll(message.content).map { it.value.split(":", " ").map { it.toLong() } }.toList()[0]
                    var clock = listOf(numbers[0], numbers[1], numbers[2], friend.id)

                    if (numbers[0] > 24 || numbers[1] > 60 || numbers[0] < 0 || numbers[1] < 0 || numbers.size != 3) {
                        toCommandSender().sendMessage("时间格式错误")
                        weather(numbers[2])
                        return@subscribeAlways
                    } else if (clock !in ct) {
                        toCommandSender().sendMessage("设置成功")
                        ct = ct.plus(listOf(listOf(numbers[0], numbers[1], numbers[2], friend.id)))
                        writer()
                    } else {
                        toCommandSender().sendMessage("已经设置过了")
                    }
                    ct = clockreader()
                }
            }
        }
        globalEventChannel().subscribeAlways<BotOnlineEvent> {
            botid = bot.id
            tempKey = keyreader()
            if (!File("config${File.separator}com.example.weather-reminder${File.separator}weather-reminder.yml").exists()) {
                writer()
            }
            weatherclock()
        }

    }
}