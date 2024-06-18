package com.example.tenniswebcrawling.macro.service

import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SlackServiceTest {

    @Autowired
    private lateinit var slackService: SlackService

    @Test
    fun startMsg() {
        slackService.startMsg()
    }

    @Test
    fun workingMsg() {
        slackService.workingMsg()
    }

    @Test
    fun sendMsg() {
        slackService.sendMsg("테스트")
    }

}