package com.km.exchange.route

import com.km.exchange.location.ById
import com.km.exchange.location.Create
import com.km.exchange.location.GetAll
import com.km.exchange.model.Sale
import com.km.exchange.storage.SaleStorage
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.delete
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route

@KtorExperimentalLocationsAPI
fun Route.sale(storage: SaleStorage) {
    get<ById> {
        call.respond(mapOf("sale" to storage.get(it.id)))
    }

    get<GetAll> {
        call.respond(mapOf("sales" to storage.getAll()))
    }

    post<ById> {
        val sale = call.receive<Sale>()
        storage.update(it.id, sale)
        call.respond(HttpStatusCode.OK, "Sale with id ${it.id} updated")
    }

    delete<ById> {
        storage.delete(it.id)
        call.respond(HttpStatusCode.OK, "Sale with id ${it.id} deleted")
    }

    post<Create> {
        val received = call.receive<Sale>()
        println("create rq sale $received")
        val id = storage.create(received)
        call.respond(HttpStatusCode.OK, "Sale id $id")
    }

}