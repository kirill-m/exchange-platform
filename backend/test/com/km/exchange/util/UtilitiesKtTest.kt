package com.km.exchange.util

import com.km.exchange.db.ExchangeDatabase
import com.km.exchange.dto.SaleTable
import com.km.exchange.dto.UserTable
import com.km.exchange.model.User
import org.jetbrains.squash.connection.transaction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UtilitiesKtTest {
    @Test
    fun testHash(): Unit {
        val storage = ExchangeDatabase(/*JDBCConnection.Companion.create(H2Dialect, pool)*/)
        storage.connection.transaction { databaseSchema().create(listOf(SaleTable, UserTable)) }

        val hash1 = hash("123456")

        storage.connection.transaction {
            storage.createUser(User("userId", "a@mail.ru", "Name", hash1))
        }

        val user1 = storage.connection.transaction {
            storage.getById("userId")
        }

        val user2 = storage.connection.transaction {
            storage.getById("userId", hash("123456"))
        }

        assertEquals(user1, user2)
    }
}