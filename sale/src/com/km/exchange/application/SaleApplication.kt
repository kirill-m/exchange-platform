package com.km.exchange.application

import com.km.exchange.route.sale
import com.km.exchange.storage.H2SaleStorage
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

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.main() {
    val storage = H2SaleStorage(/*JDBCConnection.Companion.create(H2Dialect, pool)*/)
    val registration = ImmutableRegistration.builder()
        .id("sale-$port")
        .name("sale-service")
        .address("localhost")
        .port(port.toInt())
        .build()
    Consul.builder().withUrl(consulUrlProp).build().agentClient().register(registration)

    install(ContentNegotiation) {
        gson {  }
    }
    install(Locations)

    routing {
        sale(storage)
    }
}

val Application.port get() = environment.config.property("ktor.deployment.port").getString()
val Application.consulUrlProp get() = environment.config.property("consul.url").getString()