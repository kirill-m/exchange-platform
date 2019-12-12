package com.km.exchange.main

import com.km.exchange.db.ExchangeDatabase
import com.km.exchange.dto.SaleTable
import com.km.exchange.dto.UserTable
import com.km.exchange.notification.ExchangeNotificationService
import com.km.exchange.route.createSale
import com.km.exchange.route.index
import com.km.exchange.route.login
import com.km.exchange.route.register
import com.km.exchange.util.hash
import com.km.exchange.util.hashKey
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.squash.connection.transaction

data class Session(val userId: String)

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.main() {
    val notificationService = ExchangeNotificationService()
    val storage = ExchangeDatabase(/*JDBCConnection.Companion.create(H2Dialect, pool)*/)
    storage.connection.transaction { databaseSchema().create(listOf(SaleTable, UserTable)) }

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
    install(ContentNegotiation) {
        gson {

        }
    }

//    transform.register<RpcData> {
//        TextContent(Gson().toJson(it), ContentType.Application.Json)
//    }

    routing {
        index(storage)
        createSale(storage, ::hash)
//        delete(storage, ::hash)
//        userPage(storage)
//        viewSale(storage, ::hash)
//        getAllSales(storage, ::hash)

        login(storage, ::hash)
        register(storage, ::hash, notificationService)
    }
}