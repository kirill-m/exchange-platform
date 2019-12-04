package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import com.km.exchange.model.IndexResponse
import io.ktor.application.call
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.ContentType
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.contentType

@KtorExperimentalLocationsAPI
fun Route.index(storage: ExchangeStorage) {
    get<Index> {
        call.respondHtmlTemplate(AppPage()) {
            caption { +"Exchanger" }
        }
    }

    contentType(ContentType.Application.Json) {
        get<Index> {
            //            val mySession: Session? = call.sessions.get<Session>()
//            val user = call.sessions.get<Session>().let { storage.user(it.userId) }
//            val top = storage.top(10).map(storage::getThought)
//            val latest = storage.latest(10).map(storage::getThought)
//
//            call.response.pipeline.intercept(ApplicationResponsePipeline.After) {
//                val etagString = user?.userId + "," + top.joinToString { it.id.toString() } + latest.joinToString { it.id.toString() }
//                call.response.etag(etagString)
//            }

            call.respond(IndexResponse(listOf("Test1", "Test2")))
        }
    }
}