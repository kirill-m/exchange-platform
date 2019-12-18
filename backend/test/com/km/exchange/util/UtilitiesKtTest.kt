package com.km.exchange.util

import com.km.exchange.db.ExchangeDatabase
import com.km.exchange.model.Sale
import com.km.exchange.model.User
import org.hamcrest.Matchers.*
import org.jetbrains.squash.connection.transaction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import java.time.LocalDateTime
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UtilitiesKtTest {
    companion object {
        val storage = ExchangeDatabase(/*JDBCConnection.Companion.create(H2Dialect, pool)*/)
    }


    @AfterAll
    fun tearDown() {
        storage.connection.close()
    }

    @Test
    fun testHash(): Unit {
        val hash1 = hash("123456")

        val user1 = storage.connection.transaction {
            storage.getUserById("userId")
        }
        val user2 = storage.connection.transaction {
            storage.getUserById("userId", hash("123456"))
        }

        assertEquals(user1, user2)
    }

    @Test
    fun testCreateSale(): Unit {
        val user = User("userId", "a@mail.ru", "Name Fname", hash("123456"))
        storage.createUser(user)

        val result = storage.getUserById("userId")

        assertEquals(user, result)
    }

    @Test
    fun testGetUserFromEmptyTable(): Unit {
        val result = storage.getUserById("userId")

        assertNull(result)
    }

    @Test
    fun testDeleteSale(): Unit {
        val user1 = User("userId0", "a1@mail.ru", "Name Fname0", hash("123456"))

        val createDate = LocalDateTime.now().minusDays(3)
        val sale1 = Sale(null, "userId0", "Some description0", createDate.toString())

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
        val user1 = User("userId01", "a2@mail.ru", "Name Fname0", hash("123456"))
        val user2 = User("userId11", "b2@mail.ru", "Name Fname1", hash("32rfdfdfdf"))
        val user3 = User("userId21", "c2@mail.ru", "Name Fname2", hash("12345fff6"))

        val now = LocalDateTime.now()
        val sale1 = Sale(null, "userId01", "Some description0", now.minusDays(3).toString())
        val sale2 = Sale(null, "userId11", "Some description1", now.minusDays(2).toString())
        val sale3 = Sale(null, "userId21", "Some description2", now.minusDays(1).toString())

        storage.createUser(user1)
        storage.createUser(user2)
        storage.createUser(user3)
        val createdSale1 = storage.createSale(sale1, now.minusDays(3))
        val createdSale2 = storage.createSale(sale2, now.minusDays(2))
        val createdSale3 = storage.createSale(sale3, now.minusDays(1))

        val sales = storage.getSales()

        assertThat(
            sales, containsInAnyOrder(
                allOf(
                    hasProperty("id", `is`(createdSale1)),
                    hasProperty("sellerId", `is`(sale1.sellerId))
                ),
                allOf(
                    hasProperty("id", `is`(createdSale2)),
                    hasProperty("sellerId", `is`(sale2.sellerId))
                ),
                allOf(
                    hasProperty("id", `is`(createdSale3)),
                    hasProperty("sellerId", `is`(sale3.sellerId))
                )
            )
        )
    }

    @Test
    fun testSalesNotFound() {
        val sales1 = storage.getSales("userId1")

        assertThat(sales1, empty())
    }

    @Test
    fun testGetUserSales(): Unit {
        val user1 = User("userId1", "a3@mail.ru", "Name Fname1", hash("123456"))
        val user2 = User("userId2", "b3@mail.ru", "Name Fname2", hash("4gfgf"))
        val now = LocalDateTime.now()
        val sale1 = Sale(null, "userId1", "Some description0", now.minusDays(3).toString())
        val sale2 = Sale(null, "userId1", "Some description1", now.minusDays(2).toString())
        val sale3 = Sale(null, "userId2", "Some description2", now.minusDays(1).toString())

        storage.createUser(user1)
        storage.createUser(user2)

        val createdSale1 = storage.createSale(sale1, now.minusDays(3))
        val createdSale2 = storage.createSale(sale2, now.minusDays(2))
        val createdSale3 = storage.createSale(sale3, now.minusDays(1))

        val sales1 = storage.getSales("userId1")
        val sales2 = storage.getSales("userId2")

        assertThat(
            sales1, containsInAnyOrder(
                allOf(
                    hasProperty("id", `is`(createdSale1)),
                    hasProperty("sellerId", `is`(sale1.sellerId))
                ),
                allOf(
                    hasProperty("id", `is`(createdSale2)),
                    hasProperty("sellerId", `is`(sale2.sellerId))
                )
            )
        )

        assertThat(
            sales2, contains(
                allOf(
                    hasProperty("id", `is`(createdSale3)),
                    hasProperty("sellerId", `is`(sale3.sellerId))
                )
            )
        )
    }
}