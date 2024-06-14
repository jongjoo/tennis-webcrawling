package com.example.tenniswebcrawling.macro.service

import com.example.tenniswebcrawling.slack.dto.AttachmentsDto
import com.example.tenniswebcrawling.slack.dto.FieldsDto
import com.example.tenniswebcrawling.slack.dto.SlackMsgDto
import com.example.tenniswebcrawling.slack.service.SendService
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class SlackService(
    private val sendService: SendService
) {

    fun sendMsg(msg: String) {
        sendService.postMsgSlack(builderSlackMsgDto(msg))
    }

    private fun builderSlackMsgDto(msg: String): SlackMsgDto {
        val nowTime = LocalDateTime.now()
        val format = nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"))

        val fields = FieldsDto(
            false, "빈자리가 생겼어요!! \n $msg \n <https://srent.y-sisul.or.kr/page/rent/s04.od.list.asp|테니스장 예약 하기>", "Tennis Notice"
        )
        val fieldsList = ArrayList<FieldsDto>()
        fieldsList.add(fields)

        val attachments = AttachmentsDto(
            fieldsList, "#87CEEB", "** 예약 가능 현황 ** \n ( $format )", "** 예약 가능 현황 **"
        )
        val attachmentsList = ArrayList<AttachmentsDto>()
        attachmentsList.add(attachments)

        val slackMsgDTO = SlackMsgDto(attachmentsList)
        return slackMsgDTO
    }
}