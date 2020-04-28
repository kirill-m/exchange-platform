package com.km.exchange.route

import com.km.exchange.const.UserLoginPrincipal
import com.km.exchange.location.v1.Index
import com.km.exchange.location.v1.Login
import com.km.exchange.location.v1.Logout
import com.km.exchange.location.v1.V1
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.html.respondHtml
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.clear
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.html.*

fun Route.index() {
    authenticate("session", optional = true) {
        get<Index> {
            if (call.principal<UserLoginPrincipal>() == null) {
                call.respondRedirect("/login")
            } else {
                call.respondRedirect("/v1/sales")
            }
        }
    }
}

fun Route.login() {
    get<Login> {
        call.respondHtml {
            body {
                form(method = FormMethod.post) {
                    val parameters = call.request.queryParameters
                    val errorMsg = when {
                        "invalid" in parameters -> "Incorrect login or password"
                        "no" in parameters -> "You need to be logged to do that"
                        else -> null
                    }
                    if (errorMsg != null) {
                        div {
                            style = "color:red;"
                            +errorMsg
                        }
                    }
                    textInput(name = "username") {
                        placeholder = "Username"
                    }
                    br
                    passwordInput(name = "password") {
                        placeholder = "Password"
                    }
                    br
                    submitInput {
                        value = "Log in"
                    }
                }
            }
        }
    }
    authenticate("authForm") {
        post<Login> {
            val principal = call.principal<UserLoginPrincipal>()!!
            call.sessions.set(principal)
            call.respondRedirect("/v1/sales") //TODO str literals to constants
        }
    }
}

fun Route.logout() {
    post<Logout> {
        call.sessions.clear<UserLoginPrincipal>()
        call.respondRedirect("/login")
    }
}

fun Route.sales(httpClient: HttpClient) {
    get<V1.Sales> {
        val sales = httpClient.get<List<Sale>>("http://sale-service/sale/all")
        call.respond(sales)
    }
}

fun Route.apiGateway(httpClient: HttpClient) {

}

data class Sale(val userId: Long, val title: String, val price: Int, val description: String?)