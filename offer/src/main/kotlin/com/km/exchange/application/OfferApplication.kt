package com.km.exchange.application

import com.km.exchange.route.offer
import com.km.exchange.storage.H2OfferStorage
import com.orbitz.consul.Consul
import com.orbitz.consul.model.agent.ImmutableRegistration
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalLocationsAPI
fun Application.main() {
    val storage = H2OfferStorage(/*JDBCConnection.Companion.create(H2Dialect, pool)*/)
    val registration = ImmutableRegistration.builder()
        .id("offer-$port")
        .name("offer-service")
        .address("localhost")
        .port(port.toInt())
        .build()
    Consul.builder().withUrl(consulUrlProp).build().agentClient().register(registration)

    install(ContentNegotiation) {
        gson {}
    }

    install(Locations)

    routing {
        offer(storage)
    }
}

val Application.port get() = environment.config.property("ktor.deployment.port").getString()
val Application.consulUrlProp get() = environment.config.property("consul.url").getString()