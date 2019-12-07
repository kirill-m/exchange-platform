package org.jetbrains.demo.thinkter

import jetbrains.demo.thinkter.model.Thought
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

suspend fun postSalePrepare(): PostThoughtToken =
    getAndParseResult("/sale/create", null, ::parseNewPostTokenResponse)

suspend fun postSale(replyTo: Int?, text: String, token: PostThoughtToken): Thought =
    postAndParseResult("/sale/create", URLSearchParams().apply {
        append("text", text)
        append("date", token.date.toString())
        append("code", token.code)
        if (replyTo != null) {
            append("replyTo", replyTo.toString())
        }
    }, ::parsePostThoughtResponse)

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
    val top = json.top as Array<dynamic>
    val latest = json.latest as Array<dynamic>

    return IndexResponse(top.map(::parseThought), latest.map(::parseThought))
}

private fun parsePostThoughtResponse(json: dynamic): Thought {
    return parseThought(json.thought)
}

private fun parseThought(json: dynamic): Thought {
    return Thought(json.id, json.userId, json.text, json.date, json.replyTo)
}

private fun parseNewPostTokenResponse(json: dynamic): PostThoughtToken {
    return PostThoughtToken(json.user, json.date, json.code)
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

    console.log("TEST1")
    val await = response.json().await()
    console.log("TEST1")
    return parse(await)
}
