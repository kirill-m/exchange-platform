package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import com.km.exchange.main.Session
import com.km.exchange.model.CreateSaleResponse
import com.km.exchange.model.CreateSaleToken
import com.km.exchange.model.Sale
import com.km.exchange.util.securityCode
import com.km.exchange.util.verifyCode
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import java.time.LocalDateTime

fun Route.createSale(storage: ExchangeStorage, hash: (String) -> String) {
    get<CreateSale> {
        val user = call.sessions.get<Session>()?.let { storage.getById(it.userId) }

        if (user == null) {
            call.respond(HttpStatusCode.Forbidden)
        } else {
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hash)
            call.respond(CreateSaleToken(user.userId, date, code))
        }
    }

    post<CreateSale> {
        val user = call.sessions.get<Session>()?.let { session -> storage.getById(session.userId) }

        val params = call.receiveParameters()
        val date = params["date"]
        val code = params["code"]
        val description = params["description"]
        checkNotNull(date)
        checkNotNull(code)
        checkNotNull(description)

        if (user == null || !call.verifyCode(date.toLong(), user, code, hash)) {
            call.respond(HttpStatusCode.Forbidden)
        } else {
            val createDate = LocalDateTime.now()
            val sale = Sale(user.userId, description, createDate.toString())
            storage.createSale(sale, createDate)
            call.respond(CreateSaleResponse(sale))
        }
    }
}