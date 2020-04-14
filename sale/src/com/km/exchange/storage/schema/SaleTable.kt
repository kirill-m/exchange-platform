package com.km.exchange.storage.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object SaleTable : LongIdTable() {
    val userId = long("user_id").index()
    val title = varchar("title", 30)
    val description = varchar("description", 1024).nullable()
    val price = integer("price")
}