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

    fun startMsg() {
        sendService.postMsgSlack(builderStartMsgDto())
    }

    fun workingMsg() {
        sendService.postMsgSlack(builderWorkingMsgDto())
    }

    fun sendMsg(msg: String) {
        sendService.postMsgSlack(builderSlackMsgDto(msg))
    }

    private fun getCurrentFormattedTime(): String {
        val nowTime = LocalDateTime.now()
        return nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"))
    }

    private fun buildFieldsDto(title: String, value: String, short: Boolean = false): FieldsDto {
        return FieldsDto(short, title, value)
    }

    private fun buildAttachmentsDto(
        fields: List<FieldsDto>,
        color: String,
        pretext: String,
        text: String
    ): AttachmentsDto {
        return AttachmentsDto(fields, color, pretext, text)
    }

    private fun buildSlackMsgDto(fields: List<FieldsDto>, color: String, pretext: String, text: String): SlackMsgDto {
        val attachments = buildAttachmentsDto(fields, color, pretext, text)
        return SlackMsgDto(listOf(attachments))
    }

    private fun builderWorkingMsgDto(): SlackMsgDto {
        val format = getCurrentFormattedTime()
        val fields = listOf(buildFieldsDto("", format))
        return buildSlackMsgDto(fields, "#6633FF", "[ 테니스 매크로가 동작 중입니다. ]", "")
    }

    private fun builderStartMsgDto(): SlackMsgDto {
        val format = getCurrentFormattedTime()
        val fields = listOf(buildFieldsDto("", format))
        return buildSlackMsgDto(fields, "#0033CC", "[ 테니스 매크로가 시작되었습니다. ]", "")
    }

    private fun builderSlackMsgDto(msg: String): SlackMsgDto {
        val format = getCurrentFormattedTime()
        val fields = listOf(
            buildFieldsDto(
                "** 예약 가능 현황 ** \n" +
                    " ( $format ) \n $msg \n <https://srent.y-sisul.or.kr/page/rent/s04.od.list.asp|테니스장 예약 하기>",
                "Tennis Notice"
            )
        )
        return buildSlackMsgDto(fields, "#FF0000", "빈자리가 생겼어요!!", "빈자리가 생겼어요!!")
    }

}