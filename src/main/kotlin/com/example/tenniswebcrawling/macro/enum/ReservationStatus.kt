package com.example.tenniswebcrawling.macro.enum

enum class ReservationStatus(val displayName: String) {
    AVAILABLE("예약가능"),
    COMPLETED("예약완료"),
    PERIOD_EXPIRED("기간종료");

    override fun toString(): String {
        return displayName
    }
}