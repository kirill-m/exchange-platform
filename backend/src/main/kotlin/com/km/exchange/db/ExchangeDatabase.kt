package com.km.exchange.db

import com.km.exchange.dto.UserTable
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
import org.jetbrains.squash.statements.insertInto
import org.jetbrains.squash.statements.values

class ExchangeDatabase(val connection: DatabaseConnection = H2Connection.createMemoryConnection()) : ExchangeStorage {
    constructor(dir: File) : this(H2Connection.create("jdbc:h2:file:${dir.canonicalFile.absolutePath}"))

    @KtorExperimentalAPI
    override fun getById(userId: String, hashedPassword: String?): User? =
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
}
