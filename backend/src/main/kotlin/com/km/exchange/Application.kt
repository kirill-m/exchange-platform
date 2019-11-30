package com.km.exchange

import com.google.gson.Gson
import com.km.exchange.db.ExchangeDatabase
import io.ktor.locations.Locations
//import org.jetbrains.ktor.application.Application
//import org.jetbrains.ktor.application.call
//import org.jetbrains.ktor.application.install
//import org.jetbrains.ktor.content.TextContent
////import org.jetbrains.ktor.features.*
//import org.jetbrains.ktor.http.ContentType
//import org.jetbrains.ktor.http.HttpStatusCode
//import io.ktor.locations.*
//import org.jetbrains.ktor.logging.CallLogging
//import org.jetbrains.ktor.routing.routing
//import org.jetbrains.ktor.sessions.SessionCookieTransformerMessageAuthentication
//import org.jetbrains.ktor.sessions.SessionCookiesSettings
//import org.jetbrains.ktor.sessions.withCookieByValue
//import org.jetbrains.ktor.sessions.withSessions
//import org.jetbrains.ktor.transform.transform

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.*

data class Session(val userId: String)

fun Application.main() {
//    val storage = ExchangeDatabase(/*JDBCConnection.Companion.create(H2Dialect, pool)*/)

    install(DefaultHeaders)
    install(CallLogging)
    install(ConditionalHeaders)
    install(PartialContent)
    install(Compression)
    install(Locations)
    install(Sessions) {
        cookie<Session>("SESSION") {
            transform(SessionTransportTransformerMessageAuthentication(hashKey))
        }
    }
    install(StatusPages) {
        exception<NotImplementedError> { call.respond(HttpStatusCode.NotImplemented) }
    }

//    transform.register<RpcData> {
//        TextContent(Gson().toJson(it), ContentType.Application.Json)
//    }

    routing {
//        index(storage)
//        postThought(storage, ::hash)
//        delete(storage, ::hash)
//        userPage(storage)
//        viewThought(storage, ::hash)
//
//        login(storage, ::hash)
//        register(storage, ::hash)
    }
}