package com.km.exchange.storage.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object OfferTable : LongIdTable() {
    val saleId = long("sale_id").index()
    val userId = long("user_id").index()
    val status = integer("status")
    val offeredPrice = integer("offered_price")
}