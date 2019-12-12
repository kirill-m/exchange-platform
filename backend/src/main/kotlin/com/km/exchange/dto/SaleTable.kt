package com.km.exchange.dto

import org.jetbrains.squash.definition.*

object SaleTable : TableDefinition() {
    val id = integer("id").primaryKey().autoIncrement()
    val sellerId = reference(UserTable.id)
    val createDate = datetime("create_date")
    val description = text("description")
}