package com.km.exchange.model

data class User(val email: String, val name: String, val city: String, val regDate: String?)
data class Sale(val userId: Long, val title: String, val price: Int, val description: String?)
data class Offer(val userId: Long, val saleId: Long, val status: Status, val offeredPrice: Int)

enum class Status(val code: Int) {
    CREATED(0), ACCEPTED(1), CANCELED(2), DECLINED(3);
}