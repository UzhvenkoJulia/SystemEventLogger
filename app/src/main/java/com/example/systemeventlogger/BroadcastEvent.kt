package com.example.systemeventlogger

import java.util.UUID

data class BroadcastEvent(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val timestamp: String,
    val details: String
)