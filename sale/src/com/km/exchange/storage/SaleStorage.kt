package com.km.exchange.storage

import com.km.exchange.model.Sale

interface SaleStorage {
    fun get(saleId: Long): Sale?
    fun getAll(): List<Sale>
    fun delete(saleId: Long)
    fun update(saleId: Long, sale: Sale)
    fun create(sale: Sale) : Long
    fun getByUserId(userId: Long): List<Sale>
}
