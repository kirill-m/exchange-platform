package com.km.exchange.storage.dto

import com.km.exchange.storage.schema.SaleTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SaleEntity(saleId: EntityID<Long>) : LongEntity(saleId){
    companion object : LongEntityClass<SaleEntity>(SaleTable)

    var title by SaleTable.title
    var description by SaleTable.description
    var price by SaleTable.price
}