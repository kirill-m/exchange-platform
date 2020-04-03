package com.km.exchange.storage

import com.km.exchange.model.Offer

interface OfferStorage {
    fun get(id: Long) : Offer?
    fun getByUserId(userId: Long) : List<Offer>
    fun getBySaleId(saleId: Long) : List<Offer>
    fun update(offerId: Long, offer: Offer)
    fun create(offer: Offer): Long
}