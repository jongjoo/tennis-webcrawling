package com.example.tenniswebcrawling.macro.service

import com.example.tenniswebcrawling.macro.dto.TimeDto
import com.example.tenniswebcrawling.macro.enum.ReservationStatus
import com.example.tenniswebcrawling.macro.enum.TimeSlot
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalTime
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@Service
class SisulService(
    private val slackService: SlackService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val SISUL_URI = "https://srent.y-sisul.or.kr/page/rent/s04.od.list.asp"
    private val MONTH = "" // "?sch_sym=2024-06"

    val historyMap = hashMapOf<Any, Int>()

    fun run() {
        slackService.startMsg()
        // tennis task
        scheduleSecondsTask()
        // job monitor
        scheduleHourlyTask()
    }

    private fun scheduleSecondsTask(){
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                availableTime()
            }
        }
        val timer = Timer("sisul")
        timer.schedule(timerTask, 1000, 5000)
    }

    fun availableTime() {
        val availableTime = crawling()
        val goTimeList = availableTime.filter {
            it.hour.contains(TimeSlot.SLOT_1800_2000.displayName)
                || it.hour.contains(TimeSlot.SLOT_2000_2200.displayName)
        }

        if (goTimeList.isEmpty()) {
            log.info("이용 가능한 시간이 없음")
        } else {
            val msg = goTimeList.joinToString(separator = "\n") { "${it.day}일, ${it.hour}시 타임 예약가능합니다!!" }
            val messageCount = historyMap.getOrDefault(msg, 0)
            if (messageCount > 4) {
                log.info("이미 보낸 메세지 입니다.")
            } else {
                slackService.sendMsg(msg)
                historyMap[msg] = messageCount + 1
            }
        }
    }


    fun crawling(): ArrayList<TimeDto> {
        val doc = Jsoup.connect(SISUL_URI + MONTH).post()

        doc.select("div[class=calendar1_table]").attr("class")
        val table = doc.selectFirst("div.calendar1_table div.autoscroll_x2 table.tbl_scm1")

        val resultList = ArrayList<TimeDto>()
        if (table != null) {
            // Select all td elements within the table
            val tdElements = table.select("td")

            for (td in tdElements) {
                val dateElement = td.selectFirst("h6")
                if (dateElement != null) {
                    val date: String = dateElement.text()

                    val liElements = td.select("li")
                    for (li in liElements) {
                        val timeSlot: String = li.ownText()
                        val statusElement = li.selectFirst("span")

                        if (statusElement != null && statusElement.text()
                                .contains(ReservationStatus.AVAILABLE.displayName)
                        ) {
                            resultList.add(TimeDto(date, timeSlot, statusElement.text()))
                        }
                    }
                }
            }
        }
        return resultList
    }

    fun scheduleHourlyTask() {
        val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

        val task = Runnable {
            val now = LocalTime.now()
            val start = LocalTime.of(8, 0)
            val end = LocalTime.of(23, 0)

            if (now.isAfter(start) && now.isBefore(end.plusMinutes(1))) {
                workingTime()
            }
        }

        val initialDelay = calculateInitialDelay()
        val period = 1L // Run every hour

        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.HOURS)
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalTime.now()
        val nextHour = now.plusHours(1).withMinute(0).withSecond(0).withNano(0)
        return java.time.Duration.between(now, nextHour).seconds
    }

    private fun workingTime(){
        slackService.workingMsg()
    }
}