package com.km.exchange.location

import io.ktor.locations.Location

@Location("/offer/{id}")
data class ById(val id: Long) {
    @Location("/cancel")
    data class Cancel(val byId: ById)

    @Location("/accept")
    data class Accept(val byId: ById)

    @Location("/decline")
    data class Decline(val byId: ById)

    @Location("/edit")
    data class Edit(val byId: ById)
}

@Location("/offer/user/{userId}")
data class ByUserId(val userId: Long)

@Location("/offer/sale/{saleId}")
data class BySaleId(val saleId: Long)

@Location("/offer/new")
class Create()
