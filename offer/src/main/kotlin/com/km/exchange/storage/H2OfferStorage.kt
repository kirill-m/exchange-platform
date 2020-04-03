package com.km.exchange.storage

import com.km.exchange.model.Offer
import com.km.exchange.model.Status
import com.km.exchange.model.getByCode
import com.km.exchange.storage.dao.OfferEntity
import com.km.exchange.storage.schema.OfferTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.lang.RuntimeException

class H2OfferStorage(
    val connection: Database = Database.connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver"
    )
) : OfferStorage {
    constructor(dir: File) : this(
        Database.connect("jdbc:h2:file:${dir.canonicalFile.absolutePath}", driver = "org.h2.Driver")
    )

    init {
        transaction {
            SchemaUtils.create(OfferTable)
        }
    }

    override fun get(id: Long): Offer? {
        return transaction {
            OfferEntity.findById(id)?.let { o -> Offer(o.userId, o.saleId, getByCode(o.status), o.price) }
        }
    }

    override fun getByUserId(userId: Long): List<Offer> {
        return transaction {
            OfferEntity.find { OfferTable.userId eq userId }
                .map { o -> Offer(o.userId, o.saleId, getByCode(o.status), o.price) }
        }
    }

    override fun getBySaleId(saleId: Long): List<Offer> {
        return transaction {
            OfferEntity.find { OfferTable.saleId eq saleId }
                .map { o -> Offer(o.userId, o.saleId, getByCode(o.status), o.price) }
        }
    }

    override fun update(offerId: Long, offer: Offer) {
        transaction {
            val offerEntity =
                OfferEntity.findById(offerId) ?: throw RuntimeException("Offer with id $id does not exist")
            offerEntity.status = offer.status.code
            offerEntity.price = offer.offeredPrice
        }
    }

    override fun create(offer: Offer): Long {
        val offerEntity = transaction {
            OfferEntity.new {
                saleId = offer.saleId
                userId = offer.userId
                status = Status.CREATED.code
                price = offer.offeredPrice
            }
        }

        return offerEntity.id.value
    }


}