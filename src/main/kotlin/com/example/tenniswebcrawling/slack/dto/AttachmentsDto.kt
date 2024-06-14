package com.example.tenniswebcrawling.slack.dto

data class AttachmentsDto(
    val fields: List<FieldsDto>,
    val color: String,
    val pretext: String,
    val fallback: String
)
