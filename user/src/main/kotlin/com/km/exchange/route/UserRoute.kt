package com.km.exchange.route

import com.km.exchange.location.ById
import com.km.exchange.location.Create
import com.km.exchange.model.User
import com.km.exchange.storage.UserStorage
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route

fun Route.user(storage: UserStorage) {
    get<ById> {
        call.respond(mapOf("user" to storage.get(it.id)))
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
        println("create rq offer $offer")
        val id = storage.create(offer)
        call.respond(HttpStatusCode.OK, "Offer id $id")
    }
}

