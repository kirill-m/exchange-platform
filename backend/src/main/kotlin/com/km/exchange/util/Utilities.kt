package com.km.exchange.util

import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@KtorExperimentalAPI
val hashKey = hex("6819b57a326945c1968f45236589")
@KtorExperimentalAPI
val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")
@KtorExperimentalAPI
fun hash(password: String): String {
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}

private val userIdPattern = "[a-zA-Z0-9_.]+".toRegex()
internal fun userNameValid(userId: String) = userId.matches(userIdPattern)