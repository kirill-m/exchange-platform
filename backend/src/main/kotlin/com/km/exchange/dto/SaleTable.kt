package com.km.exchange.dto

import org.jetbrains.squash.definition.*

object SaleTable : TableDefinition() {
    val id = long("id").primaryKey()
    val author = long("author_id").index()
    val creationDate = datetime("create_date")
    val description = text("description")
}