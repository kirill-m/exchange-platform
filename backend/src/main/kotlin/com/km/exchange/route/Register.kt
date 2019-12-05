package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import com.km.exchange.main.Session
import com.km.exchange.model.LoginResponse
import com.km.exchange.model.RegisterResponse
import com.km.exchange.model.User
import com.km.exchange.util.userNameValid
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Route
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set

fun Route.register(storage: ExchangeStorage, hash: (String) -> String) {
    post<Register> { _ ->
        val user = call.sessions.get<Session>()?.let { storage.getById(it.userId) }

        val params = call.receiveParameters()
        val userId = params["userId"]
        val password = params["password"]
        val email = params["email"]
        val displayName = params["displayName"]

        if (user != null) {
            call.respondRedirect("/login")
        } else {
            checkNotNull(userId)
            checkNotNull(email)
            checkNotNull(password)
            if (password != null && password.length < 6) {
                call.respond(RegisterResponse(error = "Password should be at least 6 characters long"))
            } else if (userId != null && userId.length < 4) {
                call.respond(RegisterResponse(error = "Login should be at least 4 characters long"))
            } else if (userId != null &&!userNameValid(userId)) {
                call.respond(RegisterResponse(error = "Login should be consists of digits, letters, dots or underscores"))
            } else if (userId != null && storage.getById(userId) != null) {
                call.respond(RegisterResponse(error = "User with the following login is already registered : ${storage.getById(userId)!!.passwordHash}"))
            } else {
                try {
                    val newUser = User(userId, email, displayName, hash(password))
                    storage.createUser(newUser)
                    call.sessions.set(Session(newUser.userId))
                    call.respond(LoginResponse(newUser))
                } catch (e: Throwable) {
                    if (storage.getById(userId) != null) {
                        call.respond(LoginResponse(error = "User with the following login is already registered"))
                    } else if (storage.userByEmail(email) != null) {
                        call.respond(LoginResponse(error = "User with  email ${email} is already registered"))
                    } else {
                        application.environment.log.error("Failed to register user", e)
                        call.respond(LoginResponse(error = "Failed to register. Cause: ${e.cause}"))
                    }
                }
            }
        }
    }

    get<Register> {
        call.respond(HttpStatusCode.MethodNotAllowed)
    }

}