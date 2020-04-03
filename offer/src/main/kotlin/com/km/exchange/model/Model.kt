package com.km.exchange.model

data class Offer(val userId: Long, val saleId: Long, var status: Status = Status.CREATED, var offeredPrice: Int)
data class PriceParams(val price: Int)

enum class Status(val code: Int) {
    CREATED(0), ACCEPTED(1), CANCELED(2), DECLINED(3);

}

fun getByCode(code: Int) : Status {
    return Status.values().first { s -> s.code == code }
}
