package com.km.exchange.route

import io.ktor.locations.Location
import java.time.LocalDateTime

@Location("/")
class Index()

@Location("/poll")
class Poll(val lastTime: String = "")

@Location("/login")
data class Login(val userId: String = "", val password: String = "", val error: String = "")

@Location("/register")
data class Register(val userId: String = "", val displayName: String = "", val email: String = "", val password: String = "", val error: String = "")

@Location("/logout")
class Logout()

@Location("/sale/all")
class Sales()

@Location("/sale/create")
data class CreateSale(val date: Long = 0L, val code: String = "", val description: String = "", val price: Int = 0)

@Location("/sale/{id}/delete")
data class DeleteSale(val id : Int)

@Location("/sale/{id}")
data class GetSale(val id : Int)

@Location("/order/create")
data class CreateOrder(val userId : String, val saleId: Int, val offeredPrice: Int)

@Location("order/{id}/accept")
data class AcceptOrder(val orderId: Int)

@Location("order/{id}/decline")
data class DeclineOrder(val orderId: Int)
