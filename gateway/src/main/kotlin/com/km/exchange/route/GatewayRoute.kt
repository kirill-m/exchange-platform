package com.km.exchange.route

import com.km.exchange.const.AuthName
import com.km.exchange.const.User
import com.km.exchange.const.UserLoginPassPrincipal
import com.km.exchange.location.v1.*
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
import io.ktor.sessions.clear
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.html.*

fun Route.index() {
    authenticate("session", optional = true) {
        get<Index> {
            if (call.principal<UserLoginPassPrincipal>() == null) {
                call.respondRedirect("/login")
            }
            call.respondRedirect("/v1/sales")
        }
    }
}

fun Route.login(httpClient: HttpClient) {
    get<Login> {
        if (call.principal<UserLoginPassPrincipal>() != null) {
            call.respondRedirect("/v1/sales")
        }
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
                    form(action = "/register", method = FormMethod.get) {
                        submitInput {
                            value = "Register"
                        }
                    }
                }
            }
        }
    }
    authenticate(AuthName.LOGIN_FORM) {
        post<Login> {
            // TODO get from user acc service
            val principal = call.principal<UserLoginPassPrincipal>()!!
            println("Principal ${principal}")
            val url = "/v1/account-service/user?userId=${principal.name}&password=${principal.password}"
            val user = httpClient.get<User?>(url)
            if (user == null) {
                //todo return wrong creds
            }
            call.sessions.set(principal)
            call.respondRedirect("/v1/sales") //TODO str literals to constants
        }
    }
}

fun Route.register() {
    get<Register> {
        call.respondHtml {
            body {
                form(method = FormMethod.post) {
                    h1 { +"Register" }
                    p { +"Please fill this form to create an account" }
                    hr {}

                    label {
                        htmlFor = "email"
                        b { +"Email" }
                    }
                    textInput(name = "email") {
                        placeholder = "Enter email"
                    }
                    br
                    label {
                        htmlFor = "psw"
                        b { +"Password" }
                    }
                    passwordInput(name = "password") {
                        placeholder = "Enter password"
                    }
                    br
                    label {
                        htmlFor = "rep-psw"
                        b { +"Repeat password" }
                    }
                    passwordInput(name = "rep-psw") {
                        placeholder = "Repeat password"
                    }
                    hr {}
                    submitInput {
                        value = "Register"
                    }
                }

                p {
                    +"Already have an account?"
                    a(href = "#") {
                        +"SignIn"
                    }
                }
            }
        }
    }
    authenticate(AuthName.REGISTER_FORM) {
        post<Register> {
            // TODO save to user acc service
            call.respondRedirect("/v1/sales")
        }
    }
}

fun Route.logout() {
    post<Logout> {
        call.sessions.clear<UserLoginPassPrincipal>()
        call.respondRedirect("/login")
    }
}

fun Route.sales(httpClient: HttpClient) {
    get<V1.Sales> {
        if (call.principal<UserLoginPassPrincipal>() == null) {
            call.respondRedirect("/login")
        }
        val sales = httpClient.get<List<Sale>>("http://sale-service/sale/all")
        call.respond(sales)
    }
}

fun Route.apiGateway(httpClient: HttpClient) {

}

data class Sale(val userId: Long, val title: String, val price: Int, val description: String?)