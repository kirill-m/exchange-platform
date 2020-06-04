package com.km.exchange.const

import io.ktor.auth.Principal

data class UserLoginPassPrincipal(val name: String, val password: String) : Principal
data class User(val userId: String)

object AuthName {
    const val LOGIN_FORM = "authForm"
    const val REGISTER_FORM = "regForm"
}