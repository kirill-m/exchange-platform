package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import com.km.exchange.main.Session
import com.km.exchange.model.LoginResponse
import com.km.exchange.util.userNameValid
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.*

fun Route.login(storage: ExchangeStorage, hash: (String) -> String) {
    get<Login> {
        val user = call.sessions.get<Session>()?.let {
            storage.getUserById(it.userId)
        }
        if (user == null) {
            call.respond(HttpStatusCode.Forbidden)
        } else {
            call.respond(LoginResponse(user))
        }
    }

    post<Login> {
        val params = call.receiveParameters()
        val userId = params["userId"]
        val password = params["password"]
        /* TODO: validate on client */
        val user = when {
            userId == null || password == null -> null
            userId.length < 4 -> null
            password.length < 6 -> null
            !userNameValid(userId) -> null
            else -> storage.getUserById(userId, hash(password))
        }

        if (user == null) {
            call.respond(LoginResponse(error = "Invalid name or password"))
        } else {
            call.sessions.set(Session(user.userId))
            call.respond(LoginResponse(user))
        }
    }

    post<Logout> {
        call.sessions.clear<Session>()
        call.respond(HttpStatusCode.OK)
    }
}