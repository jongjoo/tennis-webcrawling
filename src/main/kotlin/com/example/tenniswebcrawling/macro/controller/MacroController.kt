package com.example.tenniswebcrawling.macro.controller

import com.example.tenniswebcrawling.macro.service.SisulService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/macro")
class MacroController(
    private val sisulService: SisulService
) {

    @GetMapping("/sul")
    fun getSul() = sisulService.run()

}