package com.example.tenniswebcrawling.macro.service

import com.example.tenniswebcrawling.macro.dto.TimeDto
import com.example.tenniswebcrawling.macro.enum.ReservationStatus
import com.example.tenniswebcrawling.macro.enum.TimeSlot
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class SisulService(
    private val slackService: SlackService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val SISUL_URI = "https://srent.y-sisul.or.kr/page/rent/s04.od.list.asp"
    private val MONTH = "?sch_sym=2024-06"

    val historySet = hashSetOf<List<String>>()

    fun run() {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                availableTime()
            }
        }
        val timer = Timer("sisul")
        timer.schedule(timerTask, 1000, 3000)
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
            val msg = goTimeList.map {
                "${it.day}일, ${it.hour}시 타임 예약가능합니다!!\n"
            }
            if (!historySet.contains(msg)) {
                slackService.sendMsg(msg.toString())
                historySet.add(msg)
            } else {
                log.info("이미 보낸 메세지 입니다.")
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
}