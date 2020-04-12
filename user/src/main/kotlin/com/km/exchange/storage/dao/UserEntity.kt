package com.km.exchange.storage.dao

import com.km.exchange.storage.schema.UserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UserTable)

    var email by UserTable.email
    var name by UserTable.name
    var city by UserTable.city
    var regDate by UserTable.regDate
}