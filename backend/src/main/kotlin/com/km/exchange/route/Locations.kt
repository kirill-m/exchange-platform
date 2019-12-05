package com.km.exchange.route

import io.ktor.locations.Location

@Location("/")
class Index()

@Location("/login")
data class Login(val userId: String = "", val password: String = "", val error: String = "")

@Location("/register")
data class Register(val userId: String = "", val displayName: String = "", val email: String = "", val password: String = "", val error: String = "")

@Location("/logout")
class Logout()

