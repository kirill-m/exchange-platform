package com.km.exchange.application

import com.km.exchange.route.user
import com.km.exchange.storage.H2UserStorage
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.routing

@KtorExperimentalLocationsAPI
fun Application.main() {
    val storage = H2UserStorage(/*JDBCConnection.Companion.create(H2Dialect, pool)*/)

    install(ContentNegotiation) {
        gson {}
    }

    install(Locations)

    routing {
        user(storage)
    }
}