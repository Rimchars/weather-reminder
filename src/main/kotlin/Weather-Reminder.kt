package com.example


import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.command.ConsoleCommandSender.sendMessage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.info
import java.io.File

object WeatherReminder : KotlinPlugin(
    JvmPluginDescription(
        id = "com.example.weather-reminder",
        name = "WeatherReminder",
        version = "0.2.0",
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
        globalEventChannel().subscribeAlways<GroupMessageEvent> {
            if (message.content.contains("帮助")) {
                toCommandSender().sendMessage("指令\n查询城市天气：天气 <城市名>\n天气提醒闹钟：时间 <时:分> <城市编码>\n设置你的key：key <你的key>\n查询所有闹钟：闹钟表\n删除闹钟：删除闹钟 <闹钟序号>\n清空闹钟：清空闹钟")
                if (tempKey == null || tempKey == "123456789") {
                    toCommandSender().sendMessage("你还没有设置key.请使用key <你的key>来设置key")
                }
            }
            if (message.content.contains("天气 ")) {
                if (tempKey == null || tempKey == "123456789") {
                    toCommandSender().sendMessage("你还没有设置key.请使用key <你的key>来设置key")
                } else {
                    val regex = Regex("天气 ")
                    //citi转换成utf8
                    val citi = regex.replace(message.content, "")
                    toCommandSender().sendMessage(weather(citi))
                }
            }
            if (message.content.contains("key")) {
                tempKey = message.content.split(" ")[1]
                writer()
                toCommandSender().sendMessage("你的key:" + keyreader() + "设置成功")
            }
            if (message.content.contains("时间 ")) {
                if (tempKey == null || tempKey == "123456789") {
                    toCommandSender().sendMessage("你还没有设置key.请使用key <你的key>来设置key")
                } else if (message.content.contains("：")) {
                    toCommandSender().sendMessage("请使用英文冒号")
                } else {
                    val regex = Regex("\\d+:\\d+ \\d+")
                    val numbers: List<Long> =
                        regex.findAll(message.content).map { it.value.split(":", " ").map { it.toLong() } }.toList()[0]
                    val clock = listOf(numbers[0], numbers[1], numbers[2], group.id)

                    if (numbers[0] > 24 || numbers[1] > 60 || numbers[0] < 0 || numbers[1] < 0 || numbers.size != 3) {
                        toCommandSender().sendMessage("时间格式错误")
                    } else if (weather(clock[2]) == "查询失败") {
                        toCommandSender().sendMessage("城市编码错误")
                    } else if (clockcheck(clock)) {
                        toCommandSender().sendMessage("这个时间已经设置过了")
                    } else {
                        ct = ct.plusElement(clock)
                        writer()
                        toCommandSender().sendMessage("设置成功")
                    }
                }
            }
            if (message.content.contains("删除闹钟")) {
                val regex = Regex("\\d+")
                val numbers: Int = regex.find(message.content)!!.value.toInt()
                if (numbers > ct.size || numbers < 0) {
                    toCommandSender().sendMessage("没有这个闹钟")
                } else {
                    ct = ct.minusElement(ct[numbers])
                    writer()
                    toCommandSender().sendMessage("删除成功")
                }
            }
            if (message.content.contains("闹钟表")) {
                ct = clockreader()
                if (ct == listOf(listOf(0, 0, 0, 0))) {
                    toCommandSender().sendMessage("你还没有设置闹钟")
                } else {
                    var clocktable = ""
                    var count = 0
                    for (i in ct) {
                        if (i != listOf(0, 0, 0, 0)) {
                            count++
                            clocktable += ("$count $i\n")
                        }
                    }
                    toCommandSender().sendMessage("闹钟表\n$clocktable")
                }
            }
            if(message.content.contains("清空闹钟")&& ct != listOf(listOf(0, 0, 0, 0))) {
                ct = listOf(listOf(0, 0, 0, 0))
                writer()
                toCommandSender().sendMessage("清空成功")
            }
        }
        globalEventChannel().subscribeAlways<FriendMessageEvent> {
            if (message.content.contains("帮助")) {
                toCommandSender().sendMessage("指令\n查询城市天气：天气 <城市名>\n天气提醒闹钟：时间 <时:分> <城市编码>\n设置你的key：key <你的key>\n查询所有闹钟：闹钟表\n删除闹钟：删除闹钟 <闹钟序号>\n清空闹钟：清空闹钟")
                if (WeatherReminder.tempKey == null|| WeatherReminder.tempKey =="123456789") {
                    toCommandSender().sendMessage("你还没有设置key.请使用key <你的key>来设置key")
                }
            }
            if (message.content.contains("天气 ")) {
                if (WeatherReminder.tempKey == null|| WeatherReminder.tempKey =="123456789") {
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
                WeatherReminder.tempKey = message.content.split(" ")[1]
                writer()
                toCommandSender().sendMessage("你的key:"+keyreader()+"设置成功")
            }
            if (message.content.contains("时间 ")) {
                if (tempKey == null || tempKey == "123456789") {
                    toCommandSender().sendMessage("你还没有设置key.请使用key <你的key>来设置key")
                }
                else if (message.content.contains("："))
                {
                    toCommandSender().sendMessage("请使用英文冒号")
                }
                else {
                    val regex = Regex("\\d+:\\d+ \\d+")
                    val numbers: List<Long> =
                        regex.findAll(message.content).map { it.value.split(":", " ").map { it.toLong() } }.toList()[0]
                    var clock = listOf(numbers[0], numbers[1], numbers[2], friend.id)

                    if (numbers[0] > 24 || numbers[1] > 60 || numbers[0] < 0 || numbers[1] < 0 || numbers.size != 3) {
                        toCommandSender().sendMessage("时间格式错误")
                    }
                    else if (weather(clock[2])=="查询失败")
                    {
                        toCommandSender().sendMessage("城市编码错误")
                    }
                    else if(clockcheck(clock)){
                        toCommandSender().sendMessage("这个时间已经设置过了")
                    }
                    else {
                        ct = ct.plusElement(clock)
                        writer()
                        toCommandSender().sendMessage("设置成功")
                    }
                }

            }
            if(message.content.contains("删除闹钟")) {
            val regex = Regex("\\d+")
            val numbers: Int = regex.find(message.content)!!.value.toInt()
            if (numbers > ct.size || numbers < 0) {
                toCommandSender().sendMessage("没有这个闹钟")
            } else {
                ct = ct.minusElement(ct[numbers])
                writer()
                toCommandSender().sendMessage("删除成功")
            }
        }
            if(message.content.contains("闹钟表")) {
                ct = clockreader()
                if (ct == listOf(listOf(0, 0, 0, 0))) {
                    toCommandSender().sendMessage("你还没有设置闹钟")
                } else {
                    var clocktable = ""
                    var count = 0
                    for (i in ct) {
                        if (i != listOf(0, 0, 0, 0)) {
                            count++
                            clocktable += ("$count $i\n")
                        }
                    }
                    toCommandSender().sendMessage("闹钟表\n$clocktable")
                }
            }
                if(message.content.contains("清空闹钟")&& ct != listOf(listOf(0, 0, 0, 0))) {
                    ct = listOf(listOf(0, 0, 0, 0))
                    writer()
                    toCommandSender().sendMessage("清空成功")

                }
            }
        globalEventChannel().subscribeAlways<BotOnlineEvent> {
            botid = bot.id
            tempKey = keyreader()
            if (!File("config${File.separator}com.example.weather-reminder${File.separator}weather-reminder.yml").exists()) {
                writer()
            }
            while(true){
                weatherclock()
                Thread.sleep(60000)
            }
        }

    }
}