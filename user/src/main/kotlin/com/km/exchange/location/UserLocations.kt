package com.km.exchange.location

import io.ktor.locations.Location

@Location("/user/{id}")
data class ById(val id: Long) {
    @Location("/edit")
    data class Edit(val byId: ById)
}

@Location("/user/sale/{saleId}")
data class BySaleId(val saleId: Long)

@Location("/user/new")
class Create
