package com.example.tenniswebcrawling.macro.enum

enum class DayOfWeek(val displayName: String) {
    SUNDAY("일"),
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토");

    override fun toString(): String {
        return displayName
    }
}
