package com.km.exchange.db

import com.km.exchange.model.User
import org.jetbrains.squash.connection.DatabaseConnection
import org.jetbrains.squash.dialects.h2.H2Connection
import java.io.File

class ExchangeDatabase(val db: DatabaseConnection = H2Connection.createMemoryConnection()) : ExchangeStorage {
    constructor(dir: File) : this(H2Connection.create("jdbc:h2:file:${dir.canonicalFile.absolutePath}"))

    override fun user(userId: String, hash: String?): User? {

    }
}