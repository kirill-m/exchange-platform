package com.km.exchange.location

import io.ktor.locations.Location

@Location("/sale/{id}")
data class ById(val id: Long)

@Location("/sale/all")
class GetAll()

@Location("/sale/new")
class Create()




