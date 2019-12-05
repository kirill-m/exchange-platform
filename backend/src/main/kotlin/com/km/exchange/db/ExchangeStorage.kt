package com.km.exchange.db

import com.km.exchange.model.User

interface ExchangeStorage {
    fun getById(userId: String, hashedPassword: String? = null): User?
    fun userByEmail(email: String): User?
    fun createUser(user: User)
}