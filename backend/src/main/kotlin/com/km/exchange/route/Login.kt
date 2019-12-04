package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import com.km.exchange.main.Session
import com.km.exchange.model.LoginResponse
import com.km.exchange.util.userNameValid
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.sessions.*
import org.eclipse.jetty.http.HttpStatus

fun Route.login(storage: ExchangeStorage, hash: (String) -> String) {
    get<Login> {
        val user = call.sessions.get<Session>()?.let {
            storage.user(it.userId) }
        if (user == null) {
            call.respond(HttpStatusCode.Forbidden)
        } else {
            call.respond(LoginResponse(user))
        }
    }

    post<Login> {
        val login = //storage.user(it.userId, hash(it.password))
            when {
            it.userId.length < 4 -> null
//            it.password.length < 6 -> null
//            !userNameValid(it.userId) -> null
            else -> storage.user(it.userId, hash(it.password))
        }

        if (login == null) {
            call.respond(LoginResponse(error = "Invalid name or password"))
        } else {
            call.sessions.set(Session(login.userId))
            call.respond(LoginResponse(login))
        }
    }

    post<Logout> {
        call.sessions.clear<Session>()
        call.respond(HttpStatusCode.OK)
    }
}