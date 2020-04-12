package com.km.exchange.storage

import com.km.exchange.model.User

interface UserStorage {
    fun get(id: Long) : User?
    fun update(userId: Long, user: User)
    fun create(user: User): Long
}