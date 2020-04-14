package com.km.exchange.storage

import com.km.exchange.model.Sale
import com.km.exchange.storage.dto.SaleEntity
import com.km.exchange.storage.schema.SaleTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

class H2SaleStorage(
    val connection: Database = Database.connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver"
    )
) : SaleStorage {
    constructor(dir: File) : this(
        Database.connect("jdbc:h2:file:${dir.canonicalFile.absolutePath}", driver = "org.h2.Driver")
    )

    init {
        transaction {
            SchemaUtils.create(SaleTable)
        }
    }

    override fun get(saleId: Long): Sale? {
        return transaction {
            SaleEntity.findById(saleId)?.let { e -> Sale(e.userId, e.title, e.price, e.description) }
        }
    }


    override fun getByUserId(userId: Long): List<Sale> {
        return transaction {
            SaleEntity.find { SaleTable.userId eq userId }.map { e -> Sale(e.userId, e.title, e.price, e.description) }
        }
    }

    override fun getAll(): List<Sale> {
        return transaction {
            val sales = SaleEntity.all()
            sales.map { s -> Sale(s.userId, s.title, s.price, s.description) }
        }
    }

    override fun delete(saleId: Long) {
        transaction { SaleEntity.findById(saleId)?.delete() }
    }

    override fun update(saleId: Long, sale: Sale) {
        transaction {
            val saleEntity = SaleEntity.findById(saleId) ?: throw RuntimeException("Sale not found by id = $saleId")
            saleEntity.title = sale.title
            saleEntity.description = sale.description
            saleEntity.price = sale.price
            saleEntity
        }
    }

    override fun create(sale: Sale): Long {
        val entity = transaction {
            SaleEntity.new {
                userId = sale.userId
                title = sale.title
                description = sale.description
                price = sale.price
            }
        }

        return entity.id.value
    }
}