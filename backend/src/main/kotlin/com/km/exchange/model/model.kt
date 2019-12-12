package com.km.exchange.model

data class User(val userId: String, val email: String, val displayName: String?, val passwordHash: String)
data class Sale(val sellerId: String, val description: String, val createDate: String)