package com.km.exchange.route

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/")
class Index()

@KtorExperimentalLocationsAPI
@Location("/login")
data class Login(val userId: String = "", val password: String = "", val error: String = "")

@KtorExperimentalLocationsAPI
@Location("/logout")
class Logout()

