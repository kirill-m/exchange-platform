package com.km.exchange.util

import com.km.exchange.db.ExchangeDatabase
import com.km.exchange.dto.SaleTable
import com.km.exchange.dto.UserTable
import com.km.exchange.model.Sale
import com.km.exchange.model.User
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
import org.jetbrains.squash.connection.transaction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import java.time.LocalDateTime
import kotlin.test.assertTrue

class UtilitiesKtTest {
    val storage = ExchangeDatabase(/*JDBCConnection.Companion.create(H2Dialect, pool)*/)

    init {
        storage.connection.transaction { databaseSchema().create(listOf(SaleTable, UserTable)) }
    }

    @AfterAll
    fun tearDown() {
        storage.connection.close()
    }

    @Test
    fun testHash(): Unit {
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

    @Test
    fun testCreateSale(): Unit {
        val user = User("userId", "a@mail.ru", "Name Fname", hash("123456"))
        storage.createUser(user)

        val result = storage.getById("userId")

        assertEquals(user, result)
    }

    @Test
    fun testDeleteSale(): Unit {
        val user1 = User("userId0", "a@mail.ru", "Name Fname0", hash("123456"))

        val createDate = LocalDateTime.now().minusDays(3)
        val sale1 = Sale("userId0", "Some description0", createDate.toString())

        storage.createUser(user1)
        val saleId = storage.createSale(sale1, createDate)
        val sales = storage.getSales("userId0")
        assertEquals(1, sales.size)

        storage.deleteSale(saleId)

        val result = storage.getSales("userId0")
        assertTrue { result.isEmpty() }
    }

    @Test
    fun testGetAllSales(): Unit {
        val user1 = User("userId0", "a@mail.ru", "Name Fname0", hash("123456"))
        val user2 = User("userId1", "b@mail.ru", "Name Fname1", hash("32rfdfdfdf"))
        val user3 = User("userId2", "c@mail.ru", "Name Fname2", hash("12345fff6"))

        val now = LocalDateTime.now()
        val sale1 = Sale("userId0", "Some description0", now.minusDays(3).toString())
        val sale2 = Sale("userId1", "Some description1", now.minusDays(2).toString())
        val sale3 = Sale("userId2", "Some description2", now.minusDays(1).toString())

        storage.createUser(user1)
        storage.createUser(user2)
        storage.createUser(user3)
        storage.createSale(sale1, now.minusDays(3))
        storage.createSale(sale2, now.minusDays(2))
        storage.createSale(sale3, now.minusDays(1))

        val sales = storage.getSales()

        assertThat(sales, contains(sale1, sale2, sale3))
    }

    @Test
    fun testSalesNotFound() {
        val sales1 = storage.getSales("userId1")

        assertThat(sales1, empty())
    }

    @Test
    fun testGetUserSales(): Unit {
        val user1 = User("userId1", "a@mail.ru", "Name Fname1", hash("123456"))
        val user2 = User("userId2", "b@mail.ru", "Name Fname2", hash("4gfgf"))
        val now = LocalDateTime.now()
        val sale1 = Sale("userId1", "Some description0", now.minusDays(3).toString())
        val sale2 = Sale("userId1", "Some description1", now.minusDays(2).toString())
        val sale3 = Sale("userId2", "Some description2", now.minusDays(1).toString())

        storage.createUser(user1)
        storage.createUser(user2)

        storage.createSale(sale1, now.minusDays(3))
        storage.createSale(sale2, now.minusDays(2))
        storage.createSale(sale3, now.minusDays(1))

        val sales1 = storage.getSales("userId1")
        val sales2= storage.getSales("userId2")

        assertThat(sales1, contains(sale1, sale2))
        assertThat(sales2, contains(sale3))
    }
}