package com.km.exchange.application

import com.km.exchange.const.AuthName
import com.km.exchange.const.UserLoginPassPrincipal
import com.km.exchange.feature.ConsulFeature
import com.km.exchange.route.*
import com.orbitz.consul.Consul
import com.orbitz.consul.model.agent.ImmutableRegistration
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.locations.Locations
import io.ktor.response.respondRedirect
import io.ktor.routing.routing
import io.ktor.sessions.SessionStorageMemory
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie

private val s = "/login?invalid"

fun Application.main() {
    val service = ImmutableRegistration.builder()
        .id("gateway-$port")
        .name("gateway-service")
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
        gson { }
    }

    install(Sessions) {
        cookie<UserLoginPassPrincipal>("auth", storage = SessionStorageMemory()) {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication){
        form(AuthName.LOGIN_FORM) {
            userParamName = "username"
            userParamName = "password"
            challenge {
                val failures = call.authentication.allFailures
                when(failures.singleOrNull()) {
                    AuthenticationFailedCause.InvalidCredentials -> call.respondRedirect("/login?invalid")
                    AuthenticationFailedCause.NoCredentials -> call.respondRedirect("/login?no")
                    else -> call.respondRedirect("/login")
                }
            }
            validate { cred: UserPasswordCredential ->
                //TODO: login service "/user/{userName}/isPresent"
                UserLoginPassPrincipal(cred.name, cred.password)
                // else null
            }
        }
        form(AuthName.REGISTER_FORM) {
            userParamName = "username"
            userParamName = "password"
            challenge {
                val failures = call.authentication.allFailures
                when(failures.singleOrNull()) {
                    AuthenticationFailedCause.InvalidCredentials -> call.respondRedirect("/login?invalid")
                    AuthenticationFailedCause.NoCredentials -> call.respondRedirect("/login?no")
                    else -> call.respondRedirect("/login")
                }
            }
            validate { cred: UserPasswordCredential ->
                //TODO: login service "/user/{userName}/isPresent"
                UserLoginPassPrincipal(cred.name, cred.password)
                // else null
            }
        }

        session<UserLoginPassPrincipal>("session") {
            challenge {
                call.respondRedirect("/login?no")
            }
            validate { principal: UserLoginPassPrincipal ->
                principal
            }
        }
    }

    install(Locations)

    routing {
        index()
        apiGateway(httpClient)
        login(httpClient)
        register()
        logout()
        sales(httpClient)
    }
}

val Application.port get() = environment.config.property("ktor.deployment.port").getString()
val Application.consulUrlProp get() = environment.config.property("consul.url").getString()
