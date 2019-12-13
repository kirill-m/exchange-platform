package com.km.exchange.db

import com.km.exchange.dto.SaleTable
import com.km.exchange.dto.UserTable
import com.km.exchange.model.Sale
import com.km.exchange.model.User
import org.jetbrains.squash.connection.DatabaseConnection
import org.jetbrains.squash.dialects.h2.H2Connection
import java.io.File
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.squash.connection.transaction
import org.jetbrains.squash.expressions.eq
import org.jetbrains.squash.query.from
import org.jetbrains.squash.query.where
import org.jetbrains.squash.results.get
import org.jetbrains.squash.schema.create
import org.jetbrains.squash.statements.deleteFrom
import org.jetbrains.squash.statements.fetch
import org.jetbrains.squash.statements.insertInto
import org.jetbrains.squash.statements.values
import java.time.LocalDateTime

class ExchangeDatabase(val connection: DatabaseConnection = H2Connection.createMemoryConnection()) : ExchangeStorage {
    constructor(dir: File) : this(H2Connection.create("jdbc:h2:file:${dir.canonicalFile.absolutePath}"))

    init {
        connection.transaction {
            databaseSchema().create(listOf(SaleTable, UserTable))
        }
    }

    @KtorExperimentalAPI
    override fun getUserById(userId: String, hashedPassword: String?): User? =
        connection.transaction {
            from(UserTable)
                .where { UserTable.id eq userId }
                .execute()
                .mapNotNull {
                    if (hashedPassword == null || it[UserTable.passwordHash] == hashedPassword) {
                        User(userId, it[UserTable.email], it[UserTable.displayName], it[UserTable.passwordHash])
                    } else {
                        null
                    }
                }
                .singleOrNull()
        }

    override fun userByEmail(email: String): User? =
        connection.transaction {
            from(UserTable)
                .where(UserTable.email eq email)
                .execute()
                .map { User(it[UserTable.id], email, it[UserTable.displayName], it[UserTable.passwordHash]) }
                .singleOrNull()
        }

    override fun createUser(user: User) =
        connection.transaction {
            insertInto(UserTable).values {
                it[id] = user.userId
                it[email] = user.email
                it[displayName] = user.displayName
                it[passwordHash] = user.passwordHash
            }.execute()
        }

    override fun getSales(): List<Sale> {
        return connection.transaction {
            from(SaleTable)
                .execute()
                .mapNotNull {
                    Sale(
                        it[SaleTable.id],
                        it[SaleTable.sellerId],
                        it[SaleTable.description],
                        it[SaleTable.createDate].toString()
                    )
                }
                .toList()
        }
    }

    override fun getSales(userId: String): List<Sale> {
        return connection.transaction {
            from(SaleTable)
                .where(SaleTable.sellerId eq userId)
                .execute()
                .mapNotNull {
                    Sale(
                        it[SaleTable.id],
                        it[SaleTable.sellerId],
                        it[SaleTable.description],
                        it[SaleTable.createDate].toString()
                    )
                }
                .toList()
        }
    }

    override fun getSale(id: Int): Sale {
        return connection.transaction {
            from(SaleTable).where(SaleTable.id eq id)
                .execute()
                .mapNotNull {
                    Sale(
                        it[SaleTable.id],
                        it[SaleTable.sellerId],
                        it[SaleTable.description],
                        it[SaleTable.createDate].toString()
                    )
                }.single()
        }
    }

    override fun createSale(sale: Sale, createDateTime: LocalDateTime): Int {
        return connection.transaction {
            insertInto(SaleTable).values {
                it[sellerId] = sale.sellerId
                it[createDate] = createDateTime
                it[description] = sale.description
            }.fetch(SaleTable.id).execute()
        }
    }

    override fun deleteSale(saleId: Int) {
        connection.transaction {
            deleteFrom(SaleTable).where(SaleTable.id eq saleId).execute()
        }
    }
}
