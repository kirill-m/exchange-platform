package com.km.exchange.route

import io.ktor.locations.Location
import java.time.LocalDateTime

@Location("/")
class Index()

@Location("/login")
data class Login(val userId: String = "", val password: String = "", val error: String = "")

@Location("/register")
data class Register(val userId: String = "", val displayName: String = "", val email: String = "", val password: String = "", val error: String = "")

@Location("/logout")
class Logout()

@Location("/sale/all")
class Sales()

@Location("/sale/create")
data class CreateSale(val userId: String = "", val description: String = "", val creationDate: LocalDateTime)

@Location("/sale/{id}/delete")
data class DeleteSale(val id : Int)

@Location("/sale/{id}")
data class GetSale(val id : Int)

