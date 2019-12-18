package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import com.km.exchange.main.Session
import com.km.exchange.notification.NotificationService
import com.km.exchange.notification.PushNotification
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions

fun Route.offerPrice(
    storage: ExchangeStorage,
    hash: (String) -> String,
    notificationService: NotificationService
) {
    get<OfferPrice> {
        call.respond(HttpStatusCode.MethodNotAllowed)
    }

    post<OfferPrice> {
        val user = call.sessions.get<Session>()?.let { session -> storage.getUserById(session.userId) }

        val params = call.receiveParameters()
        val price = params["price"]
        val saleId = params["id"]
        checkNotNull(price)

        notificationService.sendNotification(PushNotification(user?.userId, "Title", "Made offer with price $price"))

        call.respond(HttpStatusCode.OK)
    }
}