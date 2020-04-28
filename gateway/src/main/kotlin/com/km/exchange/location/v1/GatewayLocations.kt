package com.km.exchange.location.v1

import io.ktor.locations.Location

@Location("/v1")
class V1 {
    @Location("/user/{userId}")
    data class UserInfo(val userId: Long)

    @Location("/sales")
    class Sales

}

@Location("/login")
class Login

@Location("/logout")
class Logout

@Location("/register")
class Register

@Location("/")
class Index

