package com.km.exchange.model

import java.time.LocalDateTime

data class User(val userId: String, val email: String, val displayName: String?, val passwordHash: String)
data class Sale(val saleId: Long, val sellerId: String, val createDate: LocalDateTime, val description: String)