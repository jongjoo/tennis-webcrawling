package com.example.tenniswebcrawling.macro.enum

enum class TimeSlot(val displayName: String) {
    SLOT_0600_0800("06:00~08:00"),
    SLOT_0800_1000("08:00~10:00"),
    SLOT_1000_1200("10:00~12:00"),
    SLOT_1200_1400("12:00~14:00"),
    SLOT_1400_1600("14:00~16:00"),
    SLOT_1600_1800("16:00~18:00"),
    SLOT_1800_2000("18:00~20:00"),
    SLOT_2000_2200("20:00~22:00");

    override fun toString(): String {
        return displayName
    }
}
