package com.km.exchange.route

import com.km.exchange.location.ById
import com.km.exchange.location.BySaleId
import com.km.exchange.location.ByUserId
import com.km.exchange.location.Create
import com.km.exchange.model.Offer
import com.km.exchange.model.PriceParams
import com.km.exchange.model.Status
import com.km.exchange.storage.OfferStorage
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route

fun Route.offer(storage: OfferStorage) {
    get<ById> {
        call.respond(mapOf("offer" to storage.get(it.id)))
    }

    post<ById.Edit> {
        val price = call.receive<PriceParams>().price
        updateOffer(call, storage, it.byId.id) { offeredPrice = price }
    }

    post<ById.Cancel> {
        updateOffer(call, storage, it.byId.id) { status = Status.CANCELED }
    }

    post<ById.Accept> {
        updateOffer(call, storage, it.byId.id) { status = Status.ACCEPTED }
    }

    post<ById.Decline> {
        updateOffer(call, storage, it.byId.id) { status = Status.DECLINED }
    }

    get<ByUserId> {
        call.respond(storage.getByUserId(it.userId))
    }

    get<BySaleId> {
        call.respond(mapOf("offer" to storage.getBySaleId(it.saleId)))
    }

    post<Create> {
        val offer = call.receive<Offer>()
        println("create rq offer $offer")
        val id = storage.create(offer)
        call.respond(HttpStatusCode.OK, "Offer id $id")
    }
}

suspend fun updateOffer(call: ApplicationCall, storage: OfferStorage, id: Long, block: Offer.() -> Unit) {
    storage.get(id)?.apply(block)?.let { o -> storage.update(id, o) }
    call.respond(HttpStatusCode.OK, "Offer id $id")
}
