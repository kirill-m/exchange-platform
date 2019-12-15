package com.km.exchange.route

import com.km.exchange.db.ExchangeStorage
import com.km.exchange.main.Session
import com.km.exchange.model.IndexResponse
import com.km.exchange.model.PollResponse
import com.km.exchange.model.Sale
import io.ktor.application.call
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.ContentType
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.response.ApplicationSendPipeline
import io.ktor.response.etag
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.accept
import io.ktor.routing.contentType
import io.ktor.sessions.get
import io.ktor.sessions.sessions

@KtorExperimentalLocationsAPI
fun Route.index(storage: ExchangeStorage) {
    accept(ContentType.Text.Html) {
        get<Index> {
            call.respondHtmlTemplate(AppPage()) {
                caption { +"Exchanger" }
            }
        }
    }

    accept(ContentType.Application.Json) {
        get<Index> {
            val user = call.sessions.get<Session>()?.let {
                storage.getUserById(it.userId)
            }

            val sales = storage.getSales()

            call.response.pipeline.intercept(ApplicationSendPipeline.After) {
                val etagString = user?.userId + "," + sales.joinToString { it.id.toString() }
                call.response.etag(etagString)
            }

            call.respond(IndexResponse(sales))
        }

        get<Poll> { poll ->
            if (poll.lastTime.isBlank()) {
                call.respond(PollResponse(System.currentTimeMillis(), "0"))
            } else {
                val time = System.currentTimeMillis()
                val lastTime = poll.lastTime.toLong()

                val count =
                    storage.getSales().reversed().takeWhile { storage.getSale(it.id!!).toEpochMilli() > lastTime }.size

                call.respond(PollResponse(time, if (count == 10) "10+" else count.toString()))
            }
        }
    }
}

private fun Sale.toEpochMilli() = java.time.LocalDateTime.parse(createDate).atZone(java.time.ZoneId.systemDefault())
    .toInstant().toEpochMilli()