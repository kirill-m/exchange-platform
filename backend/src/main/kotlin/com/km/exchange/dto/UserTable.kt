package com.km.exchange.dto

import org.jetbrains.squash.definition.*

object UserTable : TableDefinition() {
    val id = varchar("id", 32).primaryKey()
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 128).nullable()
    val passwordHash = varchar("password_hash", 64)
}