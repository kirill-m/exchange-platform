package com.km.exchange.storage.dao

import com.km.exchange.storage.schema.OfferTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class OfferEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OfferEntity>(OfferTable)

    var saleId by OfferTable.saleId
    var userId by OfferTable.userId
    var status by OfferTable.status
    var price by OfferTable.offeredPrice
}