package com.km.exchange.db

import com.km.exchange.model.Sale
import com.km.exchange.model.User
import java.time.LocalDateTime

interface ExchangeStorage {
    fun getUserById(userId: String, hashedPassword: String? = null): User?
    fun userByEmail(email: String): User?
    fun createUser(user: User)
    fun getSales(): List<Sale>
    fun getSales(userId: String): List<Sale>
    fun getSale(id: Int): Sale
    fun createSale(sale: Sale, createDate: LocalDateTime): Int
    fun deleteSale(saleId: Int)
}