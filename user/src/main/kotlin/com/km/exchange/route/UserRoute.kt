package com.km.exchange.route

import com.km.exchange.location.ById
import com.km.exchange.location.Create
import com.km.exchange.model.Offer
import com.km.exchange.model.Sale
import com.km.exchange.model.User
import com.km.exchange.storage.UserStorage
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route

fun Route.user(storage: UserStorage, httpClient: HttpClient) {
    get<ById> {
        val user = storage.get(it.id)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound, "User with id ${it.id} not found")
        }
        val sales = httpClient.get<List<Sale>>("http://sale-service/sale/user/${it.id}")
        val offers = httpClient.get<List<Offer>>("http://offer-service/offer/user/${it.id}")
        call.respond(mapOf("user" to user, "sales" to sales, "offers" to offers))
    }

    post<ById.Edit> {
        val user = call.receive<User>()
        println("receive new price $user")
        try {
            storage.update(it.byId.id, user)
            call.respond(HttpStatusCode.OK, "Offer id ${it.byId.id}")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Cannot update user due to $e")
        }
    }

    post<Create> {
        val offer = call.receive<User>()
        val id = storage.create(offer)
        call.respond(HttpStatusCode.OK, "User id $id")
    }
}

