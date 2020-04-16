package com.km.exchange.application

import com.km.exchange.feature.ConsulFeature
import com.km.exchange.route.user
import com.km.exchange.storage.H2UserStorage
import com.orbitz.consul.Consul
import com.orbitz.consul.model.agent.ImmutableRegistration
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.main() {
    val storage = H2UserStorage(/*JDBCConnection.Companion.create(H2Dialect, pool)*/)
    val service = ImmutableRegistration.builder()
        .id("user-$port")
        .name("user-service")
        .address("localhost")
        .port(port.toInt())
        .build()
    Consul.builder().withUrl(consulUrlProp).build().agentClient().register(service)

    val httpClient = HttpClient(Apache) {
        install(ConsulFeature) {
            consulUrl = consulUrlProp
        }
        install(JsonFeature)
    }

    install(ContentNegotiation) {
        gson {}
    }

    install(Locations)

    routing {
        user(storage, httpClient)
    }
}

val Application.port get() = environment.config.property("ktor.deployment.port").getString()
val Application.consulUrlProp get() = environment.config.property("consul.url").getString()