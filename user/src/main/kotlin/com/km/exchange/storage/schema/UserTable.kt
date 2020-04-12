package com.km.exchange.storage.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object UserTable : LongIdTable() {
    val email = varchar("email", 30)
    val name = varchar("name", 30)
    val city = varchar("city", 30)
    val regDate = varchar("reg_date", 30)
}