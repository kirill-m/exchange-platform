package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import io.ktor.locations.get
import io.ktor.routing.Route

fun Route.getSales(storage: ExchangeStorage, hash: (String) -> String) {
    get<Sales> {

    }
}