package com.example.tenniswebcrawling.slack.service

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Service
class SendService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${macro.slack.url}")
    val slackUrl: String = ""

    @Value("\${macro.slack.webhook-uri}")
    val slackWebhookUrl: String = ""

    fun slackWebClient(): WebClient {
        val connectionProvider = ConnectionProvider.builder("custom")
            .maxConnections(100)
            .pendingAcquireTimeout(Duration.ofMillis(5000))
            .build()

        val httpClient = HttpClient.create(connectionProvider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 커넥션 타임아웃 (5초)
            .doOnConnected { connection ->
                connection.addHandlerLast(ReadTimeoutHandler(10)) // 읽기 타임아웃 (10초)
                connection.addHandlerLast(WriteTimeoutHandler(10)) // 쓰기 타임아웃 (10초)
            }

        return WebClient.builder()
            .baseUrl(slackUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(ReactorClientHttpConnector(httpClient))
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
                log.error("SendService.postMsgSlack() error: ${it.message}")
            }
            .onErrorComplete()
            .block()
    }

}