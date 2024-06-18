package com.example.tenniswebcrawling.macro.service

import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SisulServiceTest {

    @Autowired
    private lateinit var sisulService: SisulService


    @Test
    fun run(){
        sisulService.run()
    }

    @Test
    fun availableTime(){
        sisulService.availableTime()
    }

    @Test
    fun crawling() {
        sisulService.crawling()
    }

}