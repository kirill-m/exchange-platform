package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import com.km.exchange.main.Session
import io.ktor.application.call
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.ContentType
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.response.ApplicationSendPipeline
import io.ktor.response.etag
import io.ktor.routing.Route
import io.ktor.routing.contentType
import io.ktor.sessions.get
import io.ktor.sessions.sessions

@KtorExperimentalLocationsAPI
fun Route.index(storage: ExchangeStorage) {
    get<Index> {
        call.respondHtmlTemplate(AppPage()) {
            caption { +"Exchanger" }
        }
    }

    contentType(ContentType.Application.Json) {
        get<Index> {
//            val mySession = call.sessions.get<Session>()
            val user = call.sessions.get<Session>()?.let {
                storage.getById(it.userId)
            }
//            val top = storage.top(10).map(storage::getThought)
//            val latest = storage.latest(10).map(storage::getThought)
            val latest = storage.getSales().sortedBy { sale -> sale.createDate }.last()

            call.response.pipeline.intercept(ApplicationSendPipeline.After) {
                val etagString =
                    user?.userId + "," + latest.createDate//top.joinToString { it.id.toString() } + latest.joinToString { it.id.toString() }
                call.response.etag(etagString)
            }
        }
    }
}