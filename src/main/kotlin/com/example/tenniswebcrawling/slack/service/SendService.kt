package com.example.tenniswebcrawling.slack.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class SendService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${macro.slack.url}")
    val slackUrl: String = ""

    @Value("\${macro.slack.webhook-uri}")
    val slackWebhookUrl: String = ""

    fun slackWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(slackUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    fun postMsgSlack(body: Any) {
        slackWebClient().post()
            .uri(slackWebhookUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.TEXT_HTML)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String::class.java)
            .doOnError {
                log.error("SisAdapter.getSisArrival() error: ${it.message}")
            }
            .onErrorComplete()
            .block()
    }

}