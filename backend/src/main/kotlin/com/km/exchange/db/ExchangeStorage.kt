package com.km.exchange.db

import com.km.exchange.model.User

interface ExchangeStorage {
    fun user(userId: String, hash: String? = null): User?
}