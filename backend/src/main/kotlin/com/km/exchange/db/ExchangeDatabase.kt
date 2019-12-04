package com.km.exchange.db

import com.km.exchange.model.User
import org.jetbrains.squash.connection.DatabaseConnection
import org.jetbrains.squash.dialects.h2.H2Connection
import java.io.File
import com.km.exchange.util.hash
import io.ktor.util.KtorExperimentalAPI

class ExchangeDatabase(val db: DatabaseConnection = H2Connection.createMemoryConnection()) : ExchangeStorage {
    constructor(dir: File) : this(H2Connection.create("jdbc:h2:file:${dir.canonicalFile.absolutePath}"))

    @KtorExperimentalAPI
    override fun user(userId: String, hash: String?): User? {
        return User("testId","a@mail.ru", "Test Name", hash("123456"))
    }
}