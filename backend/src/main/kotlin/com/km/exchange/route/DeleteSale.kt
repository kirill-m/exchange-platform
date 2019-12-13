package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import com.km.exchange.main.Session
import com.km.exchange.model.CreateSaleToken
import com.km.exchange.model.RpcData
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

fun Route.deleteSale(storage: ExchangeStorage, hash: (String) -> String) {
    get<DeleteSale> {
        val user = call.sessions.get<Session>()?.let { storage.getUserById(it.userId) }

        if (user == null) {
            call.respond(HttpStatusCode.Forbidden)
        } else {
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hash)
            call.respond(CreateSaleToken(user.userId, date, code))
        }
    }

    post<DeleteSale> {
        val user = call.sessions.get<Session>()?.let { session -> storage.getUserById(session.userId) }

        val params = call.receiveParameters()
        val date = params["date"]
        val code = params["code"]
        checkNotNull(date)
        checkNotNull(code)

        if (user == null || user.userId != storage.getSale(it.id).sellerId || !call.verifyCode(date.toLong(), user, code, hash)) {
            call.respond(HttpStatusCode.Forbidden)
        } else {
            storage.deleteSale(it.id)
            call.respond(object : RpcData {})
        }
    }
}