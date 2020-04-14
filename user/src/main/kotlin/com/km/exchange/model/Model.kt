package com.km.exchange.model

data class User(val email: String, val name: String, val city: String, val regDate: String?)

data class Sale(val userId: Long, val title: String, val price: Int, val description: String?)