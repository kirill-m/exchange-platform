package org.jetbrains.demo.thinkter

import jetbrains.demo.thinkter.model.Sale
import jetbrains.demo.thinkter.model.User
import kotlinx.coroutines.await
import org.jetbrains.demo.thinkter.model.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import kotlin.browser.*
import kotlin.js.*

suspend fun index(): IndexResponse =
    getAndParseResult("/", null, ::parseIndexResponse)

suspend fun register(userId: String, password: String, displayName: String, email: String): User =
    postAndParseResult("/register", URLSearchParams().apply {
        append("userId", userId)
        append("password", password)
        append("displayName", displayName)
        append("email", email)
    }, ::parseLoginOrRegisterResponse)

suspend fun pollFromLastTime(lastTime: String = ""): String =
    getAndParseResult<String>("/poll?lastTime=$lastTime", null, { json ->
        json.count
    })

suspend fun checkSession(): User =
    getAndParseResult("/login", null, ::parseLoginOrRegisterResponse)

suspend fun login(userId: String, password: String): User =
    postAndParseResult("/login", URLSearchParams().apply {
        append("userId", userId)
        append("password", password)
    }, ::parseLoginOrRegisterResponse)

suspend fun createSalePrepare(): CreateSaleToken =
    getAndParseResult("/sale/create", null, ::parseNewPostTokenResponse)

suspend fun createSale(replyTo: Int?, text: String, token: CreateSaleToken): Sale =
    postAndParseResult("/sale/create", URLSearchParams().apply {
        append("description", text)
        append("date", token.date.toString())
        append("code", token.code)
        if (replyTo != null) {
            append("replyTo", replyTo.toString())
        }
    }, ::parseCreateSaleResponse)

suspend fun logoutUser() {
    window.fetch("/logout", object : RequestInit {
        override var method: String? = "POST"
        override var credentials: RequestCredentials? = "same-origin".asDynamic()
    }).await()
}

suspend fun deleteSale(id: Int, date: Long, code: String) =
    postAndParseResult("/sale/$id/delete", URLSearchParams().apply {
        append("date", date.toString())
        append("code", code)
    }, { Unit })

private fun parseIndexResponse(json: dynamic): IndexResponse {
    val sales = json.sales as Array<dynamic>
    return IndexResponse(sales.map(::parseSale))
}

private fun parseCreateSaleResponse(json: dynamic): Sale {
    return parseSale(json.sale)
}

private fun parseSale(json: dynamic): Sale {
    return Sale(json.id, json.sellerId, json.description, json.createDate, 0)
}

private fun parseNewPostTokenResponse(json: dynamic): CreateSaleToken {
    return CreateSaleToken(json.user, json.date, json.code)
}

private fun parseLoginOrRegisterResponse(json: dynamic): User {
    if (json.error != null) {
        throw LoginOrRegisterFailedException(json.error.toString())
    }

    return User(json.user.userId, json.user.email, json.user.displayName, json.user.passwordHash)
}

class LoginOrRegisterFailedException(message: String) : Throwable(message)

suspend fun <T> postAndParseResult(url: String, body: dynamic, parse: (dynamic) -> T): T =
    requestAndParseResult("POST", url, body, parse)

suspend fun <T> getAndParseResult(url: String, body: dynamic, parse: (dynamic) -> T): T =
    requestAndParseResult("GET", url, body, parse)

suspend fun <T> requestAndParseResult(method: String, url: String, body: dynamic, parse: (dynamic) -> T): T {
    val response = window.fetch(url, object : RequestInit {
        override var method: String? = method
        override var body: dynamic = body
        override var credentials: RequestCredentials? = "same-origin".asDynamic()
        override var headers: dynamic = json("Accept" to "application/json")
    }).await()

    console.log("$method $url")
    val await = response.json().await()
    console.log("after rq $method $url")
    return parse(await)
}
